#version 150

uniform sampler2D DiffuseSampler;
uniform sampler2D ShockwaveSampler;

in vec2 texCoord;

uniform vec2 Center;
uniform float Strength;
uniform float Radius;
uniform float Time;

out vec4 fragColor;

void main() {
    vec2 uv = texCoord;
    vec2 toCenter = uv - Center;
    float dist = length(toCenter);
    float radius = max(Radius, 0.0001);
    float ringT = clamp(1.0 - (dist / radius), 0.0, 1.0);

    vec2 ringUv = (toCenter / radius) * 0.5 + 0.5;
    vec4 ringMask = texture(ShockwaveSampler, ringUv);
    float ring = ringMask.r * ringT;

    vec2 dir = normalize(toCenter + vec2(1e-6, 0.0));
    float wave = sin((dist / radius) * 12.0 - Time * 0.15);
    vec2 refractOffset = dir * wave * Strength * ring * 0.006;

    vec3 col = texture(DiffuseSampler, uv + refractOffset).rgb;
    col += ring * Strength * 0.18;

    fragColor = vec4(col, 1.0);
}
