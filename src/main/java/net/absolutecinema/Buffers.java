package net.absolutecinema;

import org.lwjgl.system.MemoryUtil;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

public class Buffers {
    private static final List<Buffer> bufferList = new ArrayList<>();

    /**
     * Allocates a new FloatBuffer and adds it to the buffer list.
     *
     * @param size The size of the buffer.
     * @return The allocated FloatBuffer.
     */
    public static FloatBuffer floatBuffer(int size) {
        FloatBuffer buf = MemoryUtil.memAllocFloat(size);
        bufferList.add(buf);
        return buf;
    }

    /**
     * Frees the given buffer and removes it from the buffer list.
     *
     * @param pBuffer The buffer to free.
     */
    public static void free(Buffer pBuffer) {
        MemoryUtil.memFree(pBuffer);
        bufferList.remove(pBuffer);
    }

    /**
     * Frees all allocated buffers in the buffer list.
     */
    public static void freeAll() {
        for (Buffer b : bufferList) {
            MemoryUtil.memFree(b);
        }
        bufferList.clear();
    }
}
