/*
 *    Copyright  2020 Denis Kokorin, Alex Katlein
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 */

package com.github.kokorin.jaffree.ffprobe.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

class StreamingDefaultFormatParser implements StreamingFormatParser {
    private static final Logger LOGGER = LoggerFactory.getLogger(StreamingDefaultFormatParser.class);
    
    // State
    private final Deque<State> stack;
    
    private String multilinePropertyKey = null;
    private StringBuilder multilinePropertyValue = null;
    
    public StreamingDefaultFormatParser() {
        this.stack = new LinkedList<>();
        this.stack.addLast(new State("ROOT"));
    }
    
    @Override
    public String getFormatName() {
        return "default";
    }
    
    @Override
    public void pushLine(String line) {
        if (line.startsWith("[/") && line.endsWith("]")) {
            String name = line.substring(2, line.length() - 1);
            sectionEnd(name);
        } else if (line.startsWith("[") && line.endsWith("]")) {
            String name = line.substring(1, line.length() - 1);
            sectionStart(name);
        } else {
            if (isMultiline()) {
                if (line.isEmpty()) {
                    endProperty();
                } else {
                    propertyLine(line);
                }
            } else if (line.endsWith("=")) {
                String key = line.substring(0, line.length() - 1);
                startProperty(key);
            } else {
                String[] keyValue = line.split("=", 2);
                if (keyValue.length != 2) {
                    LOGGER.warn("failed to parse line, which will be ignored: {}", line);
                    return;
                }
                String key = keyValue[0];
                String value = keyValue[1];
                if (!key.contains(":")) {
                    property(key, value);
                } else {
                    String[] tagKey = key.split(":");
                    if (tagKey.length != 2) {
                        throw new RuntimeException("Wrong subsection property format: " + line);
                    }
                    
                    String tag = tagKey[0];
                    key = tagKey[1];
                    
                    tagProperty(tag, key, value);
                }
            }
        }
    }
    
    private void sectionStart(String name) {
        stack.addLast(new State(name));
    }
    
    private void sectionEnd(String name) {
        if (stack.size() < 2) {
            throw new IllegalStateException("Can't close root section");
        }
        State state = stack.pollLast();
        
        if (!state.sectionName.equals(name)) {
            throw new RuntimeException("Expecting end of " + state.sectionName + " but found " + name);
        }
        
        State parent = stack.getLast();
        parent.subSections.add(state);
    }
    
    private void property(String key, String value) {
        stack.getLast().properties.put(key, value);
    }
    
    private void startProperty(String key) {
        if (multilinePropertyKey != null || multilinePropertyValue != null) {
            throw new IllegalStateException("Property already started, but not ended: " + multilinePropertyKey);
        }
        
        multilinePropertyKey = key;
        multilinePropertyValue = new StringBuilder();
    }
    
    private void propertyLine(String line) {
        if (multilinePropertyValue.length() > 0) {
            multilinePropertyValue.append('\n');
        }
        
        multilinePropertyValue.append(line);
    }
    
    private void endProperty() {
        property(multilinePropertyKey, multilinePropertyValue.toString());
        
        multilinePropertyKey = null;
        multilinePropertyValue = null;
    }
    
    private boolean isMultiline() {
        return multilinePropertyKey != null;
    }
    
    private void tagProperty(String name, String key, String value) {
        Map<String, Map<String, String>> tags = stack.getLast().tags;
        Map<String, String> tag = tags.get(name);
        if (tag == null) {
            tag = new HashMap<>();
            tags.put(name, tag);
        }
        
        tag.put(key, value);
    }
    
    @Override
    public Data getResult() {
        if (stack.size() != 1) {
            throw new IllegalStateException("Parsing failed");
        }
        
        State root = stack.peek();
        
        stack.clear();
        multilinePropertyKey = null;
        multilinePropertyValue = null;
        
        return new Data(toSubSections(root.subSections));
    }
    
    private static DSection toSection(State state) {
        Map<String, DTag> tags = new HashMap<>();
        for (Map.Entry<String, Map<String, String>> entry : state.tags.entrySet()) {
            tags.put(entry.getKey(), new DTag(entry.getValue()));
        }
        
        Map<String, List<DSection>> subSections = toSubSections(state.subSections);
        
        return new DSection(state.properties, tags, subSections);
    }
    
    private static Map<String, List<DSection>> toSubSections(List<State> subSections) {
        Map<String, List<DSection>> result = new HashMap<>();
        
        for (State state : subSections) {
            List<DSection> list = result.get(state.sectionName);
            if (list == null) {
                list = new ArrayList<>();
                result.put(state.sectionName, list);
            }
            
            list.add(toSection(state));
        }
        
        return result;
    }
    
    private static class State {
        public final String sectionName;
        public final Map<String, String> properties = new HashMap<>();
        public final Map<String, Map<String, String>> tags = new HashMap<>();
        public final List<StreamingDefaultFormatParser.State> subSections = new ArrayList<>();
        
        public State(String sectionName) {
            this.sectionName = sectionName;
        }
    }
}
