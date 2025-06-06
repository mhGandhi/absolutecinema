#version 330 core

in vec3 FragPos;
in vec3 Normal;
in vec3 ViewPos;

uniform vec3 uBaseColor;

out vec4 FragColor;

void main()
{
    FragColor = vec4(uBaseColor, 1.0);
}
