#version 150

in vec3 Position;

uniform mat4 u_ProjMat;

out vec3 v_ViewPos;
out vec2 v_ScreenUV;

void main() {
    vec4 clipPos = u_ProjMat * vec4(Position, 1.0);
    gl_Position = clipPos;
    v_ViewPos = Position;
    v_ScreenUV = (clipPos.xy / clipPos.w) * 0.5 + 0.5;
}