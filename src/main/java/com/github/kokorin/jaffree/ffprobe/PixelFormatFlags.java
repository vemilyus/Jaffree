/*
 *    Copyright  2018 Denis Kokorin
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

package com.github.kokorin.jaffree.ffprobe;

import com.github.kokorin.jaffree.ffprobe.data.DTag;

@Deprecated
public class PixelFormatFlags {
    private final DTag tag;

    public PixelFormatFlags(DTag tag) {
        this.tag = tag;
    }

    public DTag getTag() {
        return tag;
    }

    public int getBigEndian() {
        return tag.getInteger("big_endian");
    }

    public int getPalette() {
        return tag.getInteger("palette");
    }

    public int getBitstream() {
        return tag.getInteger("bitstream");
    }

    public int getHwaccel() {
        return tag.getInteger("hwaccel");
    }

    public int getPlanar() {
        return tag.getInteger("planar");
    }

    public int getRgb() {
        return tag.getInteger("rgb");
    }

    public int getPseudopal() {
        return tag.getInteger("pseudopal");
    }

    public int getAlpha() {
        return tag.getInteger("alpha");
    }
}
