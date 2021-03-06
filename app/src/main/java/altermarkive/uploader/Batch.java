/*
This code is free  software: you can redistribute it and/or  modify it under the
terms of the GNU Lesser General Public License as published by the Free Software
Foundation,  either version  3 of  the License,  or (at  your option)  any later
version.

This code  is distributed in the  hope that it  will be useful, but  WITHOUT ANY
WARRANTY; without even the implied warranty  of MERCHANTABILITY or FITNESS FOR A
PARTICULAR PURPOSE. See the GNU Lesser  General Public License for more details.

You should have  received a copy of the GNU  Lesser General Public License along
with code. If not, see http://www.gnu.org/licenses/.
*/
package altermarkive.uploader;

import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Batch {
    private final int[] portions;
    private final Buffer[] buffers;
    private final long stamp;

    public Batch(int[] portions) {
        this.portions = portions;
        buffers = new Buffer[portions.length];
        for (int i = 0; i < portions.length; i++) {
            buffers[i] = new Buffer(portions[i]);
        }
        stamp = System.currentTimeMillis();
    }

    public boolean full(int index) {
        return buffers[index].size() >= portions[index];
    }

    public void append(int index, byte[] bytes, int size) {
        buffers[index].append(bytes, size);
    }

    public long stamp() {
        return stamp;
    }

    public void zip(ZipOutputStream stream) throws IOException {
        for (int i = 0; i < buffers.length; i++) {
            byte[] array = buffers[i].array();
            int size = buffers[i].size();
            if (0 < size) {
                ZipEntry entry = new ZipEntry(String.format("data.%02d.bin", i));
                entry.setSize(size);
                stream.putNextEntry(entry);
                stream.write(array, 0, size);
                stream.closeEntry();
            }
        }
    }
}
