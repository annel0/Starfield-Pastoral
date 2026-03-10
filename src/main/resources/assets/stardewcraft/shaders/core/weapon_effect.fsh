#version 150

uniform sampler2D DiffuseSampler;
uniform float Time;
uniform float FlowStrength;
uniform float NoiseStrength;
uniform float TextureColorStrength;
uniform float AlphaFloor;
uniform float GlowStrength;
uniform float ErosionStrength;
uniform float UseTextureAlpha;
uniform float AlphaBoost;

in vec2 texCoord;
in vec4 vertexColor;
in vec3 localPos;

out vec4 fragColor;

void main() {
    vec2 uv = texCoord;
    vec2 center = vec2(0.5, 0.5);
    vec2 to = uv - center;
    float r = length(to);

    float spin = Time * 0.4 + (1.0 - r) * 2.4 * FlowStrength;
    float s = sin(spin);
    float c = cos(spin);
    vec2 rotated = vec2(to.x * c - to.y * s, to.x * s + to.y * c) + center;

    vec2 wave = vec2(
        sin((uv.y + Time * 0.18) * 12.0),
        cos((uv.x + Time * 0.22) * 10.0)
    ) * (0.008 * FlowStrength);

    vec2 uv2 = rotated + wave;
    vec4 tex = texture(DiffuseSampler, uv2);
    float lum = max(tex.r, max(tex.g, tex.b));

    float noise = sin((uv2.x + uv2.y + Time * 0.3) * 24.0) * 0.5 + 0.5;
    float erosion = mix(1.0, smoothstep(0.2, 0.85, noise), ErosionStrength);

    float alphaTex = mix(1.0, tex.a, UseTextureAlpha);
    alphaTex = max(alphaTex, lum);
    float alpha = max(alphaTex * vertexColor.a * erosion, AlphaFloor * vertexColor.a);
    alpha = clamp(alpha * AlphaBoost, 0.0, 1.0);
    vec3 texColor = mix(vec3(1.0), tex.rgb, TextureColorStrength);
    vec3 color = vertexColor.rgb * texColor * (0.55 + 0.9 * lum);
    color *= (1.0 + GlowStrength);

    fragColor = vec4(color, alpha);
}
