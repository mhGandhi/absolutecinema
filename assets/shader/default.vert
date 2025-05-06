#version 330 core
layout (location = 0) in vec3 aPos;
layout (location = 1) in vec3 aNormal;
layout (location = 2) in vec2 aTexCoord;

uniform mat4 uViewMat;
uniform mat4 uProjectionMat;
uniform mat4 uModelMat;
uniform vec3 uCameraPos;

out vec3 FragPos;
out vec3 Normal;
out vec2 TexCoord;
out vec3 ViewPos;

void main()
{
    FragPos = vec3(uModelMat * vec4(aPos, 1.0));
    Normal = mat3(transpose(inverse(uModelMat))) * aNormal;
    TexCoord = aTexCoord;
    ViewPos = uCameraPos;

    gl_Position = uProjectionMat * uViewMat * vec4(FragPos, 1.0);
}