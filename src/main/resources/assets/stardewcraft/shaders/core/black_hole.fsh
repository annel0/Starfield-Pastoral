#version 150

uniform sampler2D DiffuseSampler;
uniform sampler2D DepthSampler;

in vec2 texCoord;

uniform vec2 Center;
uniform float Strength;
uniform float Radius;
uniform float Time;

out vec4 fragColor;

void main() {
    vec2 uv = texCoord;
    vec2 toCenter = Center - uv;
    float dist = length(toCenter);
    float t = clamp(1.0 - (dist / max(Radius, 0.0001)), 0.0, 1.0);
    float depth = texture(DepthSampler, uv).r;
    float depthFade = smoothstep(0.0, 0.6, depth);
    t *= depthFade;

    // radial pull + swirl
    float swirl = Strength * 1.15 * pow(t, 2.5);
    float angle = swirl * 6.2831 * 0.15;
    float s = sin(angle);
    float c = cos(angle);
    vec2 dir = normalize(toCenter + vec2(1e-6, 0.0));
    vec2 rotated = vec2(dir.x * c - dir.y * s, dir.x * s + dir.y * c);
    vec2 warped = uv + rotated * (Strength * 0.04 * pow(t, 2.0));

    // subtle chromatic dispersion
    vec2 dispersion = rotated * (Strength * 0.0025 * pow(t, 2.2));
    vec3 col;
    col.r = texture(DiffuseSampler, warped + dispersion).r;
    col.g = texture(DiffuseSampler, warped).g;
    col.b = texture(DiffuseSampler, warped - dispersion).b;

    // darken ring
    float dark = smoothstep(0.0, 1.0, t) * 0.25 * Strength;
    col *= (1.0 - dark);

    fragColor = vec4(col, 1.0);
}
