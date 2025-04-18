package org.sciam.model;

public record Chunk(int id,
                    long start,
                    long end) {

    public static Chunk of(int id,
                           long start,
                           long end) {
        return new Chunk(id, start, end);
    }

    @Override
    public String toString() {
        return "Chunk{" + "id=" + id + ", start=" + start + ", end=" + end + '}';
    }
}


