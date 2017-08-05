/*
 * MIT License
 *
 * Copyright (c) 2017 Distributed clocks
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */


package org.github.com.jvec.vclock;


import java.util.Map;
import java.util.TreeMap;

/**
 * This is the vector clock class, which contains a map of id and time.
 * "id" is a string representing the id of the particular clock entry.
 * "time" is a 64 bit integer denoting the current time value of a clock.
 */
public class VClockImpl implements VClock {

    private static final long serialVersionUID = 1;
    private TreeMap<String, Long> vc;

    /**
     * Returns a new vector clock map and initiliases the map containing the clocks.
     */
    public VClockImpl() {
        this.vc = clockInit();
    }

    private TreeMap<String, Long> clockInit() {
        return new TreeMap<String, Long>();
    }

    /**
     * Increments the time value of the vector clock "pid" by one.
     * If the specified id does not exist in the map, a new clock with a value of
     * one will be created.
     *
     * @param pid The process id as string representation.
     */
    @Override
    public void tick(String pid) {
        if (this.vc.containsKey(pid)) this.vc.put(pid, this.vc.get(pid) + 1);
        else this.vc.put(pid, (long) 1);

    }

    /**
     * Sets the time value of the vector clock "pid" to the value "ticks".
     * If the specified id does not exist in the map, a new clock with a value of
     * "ticks" will be created.
     *
     * @param pid   The process id as string representation.
     * @param ticks The value of time to be set as.
     */
    @Override
    public void set(String pid, long ticks) {
        if (this.vc.containsKey(pid)) this.vc.put(pid, ticks);
        else this.vc.put(pid, ticks);
    }

    /**
     * Returns a copy of the vector clock map. Both clock maps remain valid.
     */
    @Override
    public VClock copy() {
        VClockImpl clock = new VClockImpl();
        clock.vc.putAll(this.vc);
        return clock;
    }

    /**
     * Returns the current time value of the clock "pid". If this id does not
     * exist, a negative value is returned.
     *
     * @param pid The process id as string representation.
     */
    @Override
    public long findTicks(String pid) {
        long ticks = this.vc.get(pid);
        if (this.vc.get(pid) == null) {
            return -1;
        }
        return ticks;
    }

    /**
     * Returns the most recent update of all the clocks contained in the map.
     * In this context, update means the current highest time value across all
     * clocks.
     */
    @Override
    public long lastUpdate(String pid) {
        long last = 0;
        for (Map.Entry<String, Long> clock : this.vc.entrySet()) {
            if (clock.getValue() > last) {
                last = clock.getValue();
            }
        }
        return last;
    }


    /**
     * Merges the clock map "vc" with a second clock map "other". This operation
     * directly modifies "vc" and will result in "vc" encapsulating "other".
     * If both maps contain the same specific id, the higher time value will be
     * chosen.
     *
     * @param other The vector clock map to merge with.
     */
    @Override
    public void merge(VClockImpl other) {
        for (Map.Entry<String, Long> clock : other.vc.entrySet()) {
            Long time = this.vc.get(clock.getKey());
            if (time == null) {
                this.vc.put(clock.getKey(), clock.getValue());
            } else {
                if (time < clock.getValue())
                    this.vc.put(clock.getKey(), clock.getValue());
            }
        }
    }

    /**
     * Returns a string representation of the vector map in the following format:
     * {"ProcessID 1": Time1, "ProcessID 2": Time2, ...}
     */
    @Override
    public String returnVCString() {
        int mapSize = this.vc.size();
        int i = 0;
        StringBuilder vcString = new StringBuilder();
        vcString.append("{");
        for (Map.Entry<String, Long> clock : this.vc.entrySet()) {
            vcString.append("\"");
            vcString.append(clock.getKey());
            vcString.append("\":");
            vcString.append(clock.getValue());
            if (i < mapSize) vcString.append(", ");
        }
        vcString.append("}");
        return vcString.toString();
    }

    /**
     * Prints the string generated by returnVCString for a given vector map.
     */
    @Override
    public void printVC() {
        System.out.println(returnVCString());
    }

    /**
     * Get the length of the current clock map.
     */
    @Override
    public int getClockSize() {
        return this.vc.size();
    }

    /**
     * Get the current vector clock map.
     */
    @Override
    public TreeMap<String, Long> getClockMap() {
        return this.vc;
    }
}