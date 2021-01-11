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

/**
 * Represents ffprobe output format parser.
 */
public interface StreamingFormatParser {
    /**
     * Returns format name which is passed to ffprobe via <b>-print_format</b> argument.
     *
     * @return format name
     */
    String getFormatName();
    
    /**
     * Adds the specified to be parsed
     */
    void pushLine(String line);
    
    /**
     * Returns the final result
     *
     * @return the parsed data
     */
    Data getResult();
}
