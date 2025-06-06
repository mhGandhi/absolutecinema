#version 330 core

in vec3 FragPos;
in vec3 Normal;
in vec3 ViewPos;

out vec4 FragColor;

void main()
{
    vec3 viewDir = normalize(ViewPos - FragPos);
    if(dot(viewDir, Normal) <= 0){
        discard;
    }

    FragColor = vec4(Normal * 0.5 + 0.5, 1.0); // Visualize normals
}