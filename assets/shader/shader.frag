#version 330 core

in vec3 FragPos;          // Fragment position in world space
in vec3 Normal;           // Normal vector in world space
in vec3 ViewPos;

out vec4 FragColor;

void main()
{
    FragColor = vec4(Normal * 0.5 + 0.5, 1.0); // Visualize normals
}