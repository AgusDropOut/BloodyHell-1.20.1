#version 150

in vec3 Position;
in vec4 Color;
in vec2 UV0;
in ivec2 UV1;
in ivec2 UV2;
in vec3 Normal;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform float TimeAnim; // Asegurate de tener esto si lo usas luego

out vec2 texCoord0;

void main() {
    gl_Position = ProjMat * ModelViewMat * vec4(Position, 1.0);
    texCoord0 = UV0;
    // Si necesitas usar el vertexColor o las UVs de luz/overlay en el fragment shader,
    // pasalos a variables 'out' como haces en tus otros shaders.
}