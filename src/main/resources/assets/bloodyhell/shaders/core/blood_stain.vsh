#version 150

in vec3 Position;
in vec4 Color;
in vec2 UV0;

uniform mat4 RealProjMat;

out vec4 vertexColor;
out vec2 texCoord0;

void main() {
    gl_Position = RealProjMat * vec4(Position, 1.0);
    vertexColor = Color;
    texCoord0 = UV0;
}