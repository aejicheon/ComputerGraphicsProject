#version 300 es

uniform mat4 uMVPMatrix, worldMat;
uniform vec3 eyePos;

layout(location = 5) in vec4 vPosition;
layout(location = 8) in vec3 vNormal;
layout(location = 14) in vec2 vTexCoord;

out vec2 fTexCoord;
out vec3 fNormal, fView;

void main() {
    gl_Position = uMVPMatrix * vPosition;
    fTexCoord = vTexCoord;
    fNormal = normalize(transpose(inverse(mat3(worldMat))) * vNormal);
    fView = normalize(eyePos - (worldMat * vPosition).xzy);
}