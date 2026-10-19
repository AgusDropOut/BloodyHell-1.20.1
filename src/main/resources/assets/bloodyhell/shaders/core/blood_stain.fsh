#version 150

uniform float Time;
uniform float Intensity;

in vec4 vertexColor;
in vec2 texCoord0;

out vec4 fragColor;

#define TENTACLES floor(vertexColor.r * 10.0 + 0.5)
#define SEED      (vertexColor.g * 100.0)
#define COOLDOWN  vertexColor.b
#define GLOBAL_A  vertexColor.a

#define SPEED      2.0
#define FILAMENTS  15.0
#define MIST       1.0
#define SPARKS_ON  1

float hash11(float x){
    float p = fract(x * 0.1031);
    p *= p + 33.33;
    p *= p + p;
    return fract(p);
}

float hash21(vec2 p){
    vec3 p3 = fract(vec3(p.xyx) * 0.1031);
    p3 += dot(p3, p3.yzx + 33.33);
    return fract((p3.x + p3.y) * p3.z);
}

vec2 hash22(vec2 p){
    vec3 p3 = fract(vec3(p.xyx) * vec3(0.1031, 0.1030, 0.0973));
    p3 += dot(p3, p3.yzx + 33.33);
    return fract((p3.xx + p3.yz) * p3.zy);
}

float noise(vec2 p){
    vec2 i = floor(p), f = fract(p);
    f = f * f * (3.0 - 2.0 * f);
    float a = hash21(i);
    float b = hash21(i + vec2(1.0, 0.0));
    float c = hash21(i + vec2(0.0, 1.0));
    float d = hash21(i + vec2(1.0, 1.0));
    return mix(mix(a, b, f.x), mix(c, d, f.x), f.y);
}

float fbm(vec2 p){
    float v = 0.0, a = 0.5;
    mat2 m = mat2(1.6, 1.2, -1.2, 1.6);
    for (int i = 0; i < 5; i++){
        v += a * noise(p);
        p = m * p;
        a *= 0.5;
    }
    return v;
}

vec3 ramp(float h){
    vec3 c = vec3(0.05, 0.0, 0.006);
    c = mix(c, vec3(0.22, 0.0, 0.015),  smoothstep(0.00, 0.22, h));
    c = mix(c, vec3(0.78, 0.02, 0.035), smoothstep(0.20, 0.55, h));
    c = mix(c, vec3(1.0, 0.17, 0.12),  smoothstep(0.50, 0.80, h));
    c = mix(c, vec3(1.0, 0.72, 0.68),  smoothstep(0.80, 1.15, h));
    return c;
}

vec4 over(vec4 top, vec4 bot){
    return top + bot * (1.0 - top.a);
}

float filaments(vec2 dir, float rq, vec2 w, float t){
    float f = 0.0;
    for (int i = 0; i < 2; i++){
        float s = float(i);
        float freq = 3.0 + 4.0 * s;
        vec2 sp = dir * freq
        + vec2(sin(rq * (5.0 + 2.0 * s) - t * 0.7 + s * 2.0),
        cos(rq * (4.3 + s)     - t * 0.5)) * 0.22
        + w * (0.9 + 0.4 * s)
        + vec2(s * 7.3, s * 3.1);
        float n = fbm(sp + SEED);

        float ridge = 1.0 - abs(2.0 * n - 1.0);
        float pinch = smoothstep(0.2, 0.95, rq);
        ridge = max(0.0, ridge - pinch * 0.85);

        float line = pow(ridge, 14.0 + 15.0 * rq);
        float pulse = 0.65 + 0.35 * sin(rq * 22.0 - t * 3.0 + s * 1.7);
        f += line * pulse * (1.0 - 0.4 * s);
    }
    f *= smoothstep(0.03, 0.2, rq);
    return f;
}

float sparks(vec2 p, float t){
    float acc = 0.0;
    for (int i = 0; i < 3; i++){
        float s = float(i);
        vec2 uv = p * (8.0 + s * 5.0) + vec2(s * 13.1, 0.0);
        uv.y -= t * (0.6 + 0.35 * s);
        vec2 id = floor(uv);
        vec2 f  = fract(uv) - 0.5;
        vec2 h  = hash22(id + SEED);
        float on = step(0.62, hash21(id + 9.1));
        vec2 pos = (h - 0.5) * 0.6;
        pos.x += 0.12 * sin(t * 2.0 + h.y * 6.283);
        float d  = length(f - pos);
        float tw = 0.5 + 0.5 * sin(t * 5.0 + h.x * 40.0);
        acc += on * tw * pow(max(1.0 - d / (0.05 + 0.04 * h.y), 0.0), 2.0);
    }
    return acc;
}

void main() {
    vec2 p = (texCoord0 - 0.5) * 2.0;
    float t  = Time * SPEED;
    float r0 = length(p);

    vec2 w = vec2(fbm(p * 2.0 + vec2(0.0, -t * 0.25) + SEED),
    fbm(p * 2.0 + vec2(5.2, 1.3 + t * 0.2) + SEED)) - 0.5;

    w *= mix(0.1, 1.0, COOLDOWN);

    vec2 q   = p + w * (0.16 + 0.24 * r0);
    float rq = length(q);
    vec2 dir = q / max(rq, 1e-4);
    float a  = atan(q.y, q.x);

    float ph  = 0.35 * sin(t * 0.3) * mix(0.2, 1.0, COOLDOWN);
    float arg = a * TENTACLES * 0.5 + ph;
    float ac  = pow(abs(cos(arg)), 1.0 + rq * 4.0);
    float k   = mod(floor(arg / 3.14159265 + 0.5), TENTACLES);
    float len = 0.75 + 0.45 * hash11(k + 3.7 + SEED);

    float R  = 0.15 + 0.5 * len * pow(ac, 4.5) + 0.05 * fbm(dir * 4.0 + t * 0.1) * mix(0.2, 1.0, COOLDOWN);
    float d  = rq / R;

    vec2 fp = q * 3.2 - dir * t * 0.7;
    float f1 = fbm(fp);
    float f2 = fbm(fp * 2.1 + 4.0 + t * 0.1);
    float ridge = 1.0 - abs(2.0 * f1 - 1.0);
    float veins = pow(ridge, 5.0);

    float dE = d + 0.25 * (f2 - 0.5) * smoothstep(0.3, 1.0, d);
    float starMask = 1.0 - smoothstep(0.8, 1.0, dE);
    float grad = pow(clamp(1.0 - dE, 0.0, 1.0), 1.2);
    float core = exp(-rq * 5.5);

    float h = starMask * grad * (0.45 + 0.9 * f2 + veins * 0.8) + core * 1.1;

    float currentIntensity = Intensity == 0.0 ? 1.0 : Intensity;
    h *= currentIntensity * COOLDOWN;

    float fireA = clamp(starMask + core, 0.0, 1.0);

    float Rm = 0.20 + 0.95 * len * pow(ac, 1.6);
    float dm = rq / Rm;
    vec2  mp = q * 2.4 - dir * t * 0.35 + w * 1.5;
    float mn = fbm(mp);
    float mist = (1.0 - smoothstep(0.15, 1.1, dm)) * smoothstep(0.22, 0.75, mn);
    mist *= mix(0.25, 1.0, smoothstep(0.2, 0.9, d));
    float mistA = clamp(mist * 0.6 * MIST, 0.0, 1.0);
    vec3  mistCol = mix(vec3(0.55, 0.03, 0.04), vec3(0.10, 0.0, 0.01), smoothstep(0.2, 0.95, rq));
    mistCol *= 0.6 + 0.8 * mn;

    float sootD = min(dm, rq * 1.7) + 0.4 * (fbm(q * 3.5 + vec2(3.0, 7.0)) - 0.5);
    float sootA = (1.0 - smoothstep(0.45, 1.25, sootD)) * 0.85;
    vec3  sootCol = vec3(0.06, 0.0, 0.006);

    float fil = filaments(dir, rq, w, t) * FILAMENTS * currentIntensity * COOLDOWN;
    float filA = clamp(fil, 0.0, 1.0) * 0.9;
    vec3  filCol = vec3(1.0, 0.08, 0.09);

    vec4 acc = vec4(sootCol * sootA, sootA);
    acc = over(vec4(mistCol * mistA, mistA), acc);
    acc = over(vec4(ramp(h) * fireA, fireA), acc);
    acc = over(vec4(filCol * filA, filA), acc);

    #if SPARKS_ON
    float sp = sparks(p, t) * smoothstep(2.0, 0.4, dm) * COOLDOWN;
    acc.rgb += vec3(1.0, 0.3, 0.2) * sp * 1.6;
    acc.a = clamp(acc.a + sp, 0.0, 1.0);
    #endif

    acc.rgb += vec3(0.55, 0.03, 0.03) * exp(-rq * 3.5) * 0.2 * COOLDOWN;
    float edge = 1.0 - smoothstep(0.72, 1.0, r0);
    acc *= edge * GLOBAL_A;

    fragColor = vec4(acc.rgb, acc.a);
}