#version 330 core

in vec3 FragPos;
in vec3 Normal;
in vec3 ViewPos;

out vec4 FragColor;

void main()
{
    FragColor = vec4(Normal * 0.5 + 0.5, 1.0); // Visualize normals
}