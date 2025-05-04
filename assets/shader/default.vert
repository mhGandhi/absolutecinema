#version 330 core
layout (location = 0) in vec3 aPos;
layout (location = 1) in vec3 aNormal;

uniform mat4 viewMat;
uniform mat4 projectionMat;
uniform mat4 modelMat;
uniform vec3 cameraPos;

out vec3 FragPos;
out vec3 Normal;
out vec3 ViewPos;

void main()
{
    FragPos = vec3(modelMat * vec4(aPos, 1.0));
    Normal = mat3(transpose(inverse(modelMat))) * aNormal;
    ViewPos = cameraPos;

    gl_Position = projectionMat * viewMat * vec4(FragPos, 1.0);
}