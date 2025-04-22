package net.absolutecinema;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Util {
    public static float[] trisFromObj(String pPath) {
        List<Float> faces = new ArrayList<>();
        try {
            List<String> lines = Files.readAllLines(Paths.get(pPath));
            List<float[]> points = new ArrayList<>();
            List<float[]> normals = new ArrayList<>(); // For storing normals

            // Read all lines in the file
            for (String line : lines) {
                String[] parts = line.split("\\s+"); // Split by spaces (handles multiple spaces)

                if (parts.length >= 4) {
                    if (parts[0].equals("v")) { // Vertex line
                        points.add(new float[]{
                                Float.parseFloat(parts[1]),
                                Float.parseFloat(parts[2]),
                                Float.parseFloat(parts[3])
                        });
                    }
                    if (parts[0].equals("vn")) { // Normal line (if available)
                        normals.add(new float[]{
                                Float.parseFloat(parts[1]),
                                Float.parseFloat(parts[2]),
                                Float.parseFloat(parts[3])
                        });
                    }
                    if (parts[0].equals("f")) { // Face line
                        int[] indices = new int[parts.length - 1];
                        List<int[]> normalIndices = new ArrayList<>();
                        for (int i = 1; i < parts.length; i++) {
                            String[] vertexData = parts[i].split("/");

                            // Get vertex indices
                            indices[i - 1] = Integer.parseInt(vertexData[0]) - 1; // OBJ indices start at 1

                            // Get normal indices if present
                            if (vertexData.length > 2 && !vertexData[2].isEmpty()) {
                                normalIndices.add(new int[]{Integer.parseInt(vertexData[2]) - 1});
                            } else {
                                normalIndices.add(null); // No normal data, calculate later
                            }
                        }

                        // If normals are missing, calculate them
                        if (normalIndices.stream().allMatch(n -> n == null)) {
                            for (int i = 1; i < indices.length - 1; i++) {
                                float[] v1 = points.get(indices[0]);
                                float[] v2 = points.get(indices[i]);
                                float[] v3 = points.get(indices[i + 1]);

                                // Calculate the normal for this triangle
                                float[] normal = calculateNormal(v1, v2, v3);

                                // Store normals for each vertex
                                normalIndices.set(i - 1, new int[]{normals.size()});
                                normalIndices.set(i, new int[]{normals.size()});
                                normalIndices.set(i + 1, new int[]{normals.size()});
                                normals.add(normal);
                            }
                        }

                        // Convert quads into triangles and add faces
                        for (int i = 1; i < indices.length - 1; i++) {
                            addFace(faces, points.get(indices[0]), points.get(indices[i]), points.get(indices[i + 1]), normals, normalIndices.get(0)[0], normalIndices.get(i)[0], normalIndices.get(i + 1)[0]);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Convert the list to an array and return
        float[] ret = new float[faces.size()];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = faces.get(i);
        }
        return ret;
    }

    private static void addFace(List<Float> faces, float[] v1, float[] v2, float[] v3, List<float[]> normals, int n1, int n2, int n3) {
        faces.add(v1[0]); faces.add(v1[1]); faces.add(v1[2]);
        faces.add(normals.get(n1)[0]); faces.add(normals.get(n1)[1]); faces.add(normals.get(n1)[2]);
        faces.add(v2[0]); faces.add(v2[1]); faces.add(v2[2]);
        faces.add(normals.get(n2)[0]); faces.add(normals.get(n2)[1]); faces.add(normals.get(n2)[2]);
        faces.add(v3[0]); faces.add(v3[1]); faces.add(v3[2]);
        faces.add(normals.get(n3)[0]); faces.add(normals.get(n3)[1]); faces.add(normals.get(n3)[2]);
    }

    // Helper function to calculate normal of a triangle
    private static float[] calculateNormal(float[] v1, float[] v2, float[] v3) {
        // Vectors
        float[] edge1 = new float[]{v2[0] - v1[0], v2[1] - v1[1], v2[2] - v1[2]};
        float[] edge2 = new float[]{v3[0] - v1[0], v3[1] - v1[1], v3[2] - v1[2]};

        // Cross product to get normal
        float[] normal = new float[3];
        normal[0] = edge1[1] * edge2[2] - edge1[2] * edge2[1];
        normal[1] = edge1[2] * edge2[0] - edge1[0] * edge2[2];
        normal[2] = edge1[0] * edge2[1] - edge1[1] * edge2[0];

        // Normalize the normal
        float length = (float) Math.sqrt(normal[0] * normal[0] + normal[1] * normal[1] + normal[2] * normal[2]);
        normal[0] /= length;
        normal[1] /= length;
        normal[2] /= length;

        return normal;
    }
}
