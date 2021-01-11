/*
 *    Copyright  2017 Denis Kokorin
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

package com.github.kokorin.jaffree.nut;

import java.util.EnumSet;
import java.util.Set;

/**
 * Represents NUT frame code.
 * <p>
 * It describes common frame characteristics (even across multiple streams).
 * <p>
 * Note: in NUT specification there is no such class. The name comes from ffmpeg NUT implementation.
 */
@SuppressWarnings("checkstyle:visibilitymodifier")
public class FrameCode {
    public final Set<Flag> flags;

    /**
     * If {@link Flag#STREAM_ID} is not set then this is the stream number for the
     * frame following this frame_code.
     * <p>
     * If {@link Flag#STREAM_ID} is set then this value has no meaning.
     * MUST be <250.
     */
    public final int streamId;

    /**
     * If {@link Flag#SIZE_MSB} is set then data_size_msb which is stored after the
     * frame code is multiplied with it and forms the more significant part
     * of the size of the following frame.
     * <p>
     * If {@link Flag#SIZE_MSB} is not set then this field has no meaning.
     * MUST be &lt; 16384.
     */
    public final int dataSizeMul;

    /**
     * The less significant part of the size of the following frame.
     * This added together with data_size_mul*data_size_msb is the size of
     * the following frame.
     * MUST be &lt;16384.
     */
    public final int dataSizeLsb;

    /**
     * If {@link Flag#CODED_PTS} is set in the flags of the current frame then this
     * value MUST be ignored.
     * <p>
     * if {@link Flag#CODED_PTS} is not set then pts_delta is the
     * difference between the current pts and last_pts.
     * <p>
     * MUST be &lt;16384 and &gt;-16384.
     */
    public final long ptsDelta;

    /**
     * Number of reserved bytes in NUT frame.
     * <p>
     * MUST be &lt;256.
     */
    public final long reservedCount;

    /**
     * This is the time difference in stream timebase units from the pts at which
     * the output from the decoder has converged independent from the availability
     * of previous frames (that is the frames are virtually identical no matter
     * if decoding started from the very first frame or from this keyframe).
     * <p>
     * If its value is 1-(1&lt;&lt;62) then match_time_delta is unspecified, that is
     * the muxer lacked sufficient information to set it.
     * <p>
     * A muxer MUST only set it to 1-(1&lt;&lt;62) if it does not know the correct
     * value. That is, it is not allowed to randomly discard known values.
     * <p>
     * If {@link Flag#MATCH_TIME} is not set then this value shall be used for
     * match_time_delta, otherwise this value is ignored.
     * MUST be &lt;32768 and &gt;-32768 or =1-(1&lt;&lt;62).
     */
    public final long matchTimeDelta;

    /**
     * The index into the elision_header table. MUST be &lt;128.
     */
    public final long elisionHeaderIdx;


    public static final FrameCode INVALID = new FrameCode(
            EnumSet.of(Flag.INVALID),
            0, 0, 0,
            0, 0, 0, 0
    );

    /**
     * Creates FrameCode.
     *
     * @param flags            flags
     * @param streamId         stream id
     * @param dataSizeMul      data size multiplier
     * @param dataSizeLsb      data size least significant byte
     * @param ptsDelta         difference between last & current PTS
     * @param reservedCount    number of reserved bytes
     * @param matchTimeDelta   match time delta
     * @param elisionHeaderIdx index into the elision_header table
     */
    @SuppressWarnings("checkstyle:parameternumber")
    public FrameCode(final Set<Flag> flags, final int streamId, final int dataSizeMul,
                     final int dataSizeLsb, final long ptsDelta, final long reservedCount,
                     final long matchTimeDelta, final long elisionHeaderIdx) {
        this.flags = flags;
        this.streamId = streamId;
        this.dataSizeMul = dataSizeMul;
        this.dataSizeLsb = dataSizeLsb;
        this.ptsDelta = ptsDelta;
        this.reservedCount = reservedCount;
        this.matchTimeDelta = matchTimeDelta;
        this.elisionHeaderIdx = elisionHeaderIdx;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "FrameCode{"
                + "flags=" + flags
                + ", streamId=" + streamId
                + ", dataSizeMul=" + dataSizeMul
                + ", dataSizeLsb=" + dataSizeLsb
                + ", ptsDelta=" + ptsDelta
                + ", reservedCount=" + reservedCount
                + ", matchTimeDelta=" + matchTimeDelta
                + ", headerIdx=" + elisionHeaderIdx
                + '}';
    }

    /**
     * FrameCode Flags.
     */
    @SuppressWarnings("checkstyle:magicnumber")
    public enum Flag {
        KEYFRAME(1),

        /**
         * If set, the stream has no relevance on presentation. (EOR)
         * <p>
         * EOR frames MUST be zero-length and must be set keyframe.
         * All streams SHOULD end with EOR, where the pts of the EOR indicates the
         * end presentation time of the final frame.
         * <p>
         * An EOR set stream is unset by the first content frame.
         * EOR can only be unset in streams with zero decode_delay .
         */
        EOR(1 << 1),

        /**
         * If set, coded_pts is in the frame header.
         */
        CODED_PTS(1 << 3),

        /**
         * If set, stream_id is coded in the frame header.
         */
        STREAM_ID(1 << 4),

        /**
         * If set, data_size_msb is coded in the frame header, otherwise data_size_msb is 0.
         */
        SIZE_MSB(1 << 5),

        /**
         * If set, the frame header contains a headerChecksum.
         * <p>
         * MUST be set if the frame's data_size is strictly greater than
         * 2*max_distance or the difference abs(pts-last_pts) is strictly greater than
         * max_pts_distance (where pts represents this frame's pts and last_pts is
         * defined as below).
         */
        CHECKSUM(1 << 6),

        /**
         * If set, reserved_count is coded in the frame header.
         */
        RESERVED(1 << 7),

        /**
         * If set, side/meta data is stored with the frame data.
         * <p>
         * This flag MUST NOT be set in version &lt; 4
         */
        SM_DATA(1 << 8),

        /**
         * If set, header_idx is coded in the frame header.
         */
        HEADER_IDX(1 << 10),

        /**
         * If set, match_time_delta is coded in the frame header.
         */
        MATCH_TIME(1 << 11),

        /**
         * If set, coded_flags are stored in the frame header.
         */
        CODED_FLAGS(1 << 12),

        /**
         * If set, frame_code is invalid.
         */
        INVALID(1 << 13);

        private final long code;

        Flag(final long code) {
            this.code = code;
        }

        /**
         * Creates Set of {@link Flag} from passed in bitmask value.
         *
         * @param value bitmask value
         * @return flags
         */
        public static Set<Flag> fromBitCode(final long value) {
            Set<Flag> result = EnumSet.noneOf(Flag.class);
            for (Flag flag : values()) {
                if ((flag.code & value) > 0) {
                    result.add(flag);
                }
            }
            return result;
        }

        /**
         * Converts set of {@link Flag} to bitmask value.
         *
         * @param flags flags
         * @return bitmask value
         */
        public static long toBitCode(final Set<Flag> flags) {
            long result = 0;
            for (Flag flag : flags) {
                result += flag.code;
            }
            return result;
        }

        /**
         * Executes exclusive Or (Xor) operation on operands.
         *
         * @param op1 flags
         * @param op2 flags
         * @return xored flags
         */
        public static Set<Flag> xor(final Set<Flag> op1, final Set<Flag> op2) {
            Set<Flag> result = EnumSet.copyOf(op1);

            for (FrameCode.Flag codedFlag : op2) {
                if (result.contains(codedFlag)) {
                    result.remove(codedFlag);
                } else {
                    result.add(codedFlag);
                }
            }

            return result;
        }
    }
}
