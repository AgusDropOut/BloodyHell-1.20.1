#version 150

in vec3 Position;
uniform mat4 u_ProjMat;
out vec4 v_ScreenPos;

void main() {
    gl_Position = u_ProjMat * vec4(Position, 1.0);
    v_ScreenPos = gl_Position;
}