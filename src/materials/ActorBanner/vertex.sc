$input a_position, a_color0, a_texcoord0, a_indices, a_normal
#ifdef INSTANCING
  $input i_data0, i_data1, i_data2
#endif

$output v_color0, v_fog, v_light, v_texcoord0, v_texcoords

#include <bgfx_shader.sh>
#include <MinecraftRenderer.Materials/DynamicUtil.dragonh>
#include <MinecraftRenderer.Materials/TAAUtil.dragonh>
#include <MinecraftRenderer.Materials/FogUtil.dragonh>

uniform vec4 OverlayColor;
uniform vec4 TileLightColor;
uniform vec4 FogColor;
uniform vec4 FogControl;
uniform vec4 UVAnimation;
uniform mat4 Bones[8];
uniform vec4 ViewPositionAndTime;
uniform vec4 RenderDistance;
uniform vec4 CameraPosition;
uniform vec4 BannerColors[7];
uniform vec4 BannerUVOffsetsAndScales[7];

void main() {
  mat4 World = u_model[0];

  World = mul(World, Bones[int(a_indices)]);

  vec2 texcoord0 = a_texcoord0;
  texcoord0 = applyUvAnimation(texcoord0, UVAnimation);

  vec3 worldPosition;
  #ifdef INSTANCING
    mat4 model = mtxFromCols(i_data0, i_data1, i_data2, vec4(0.0, 0.0, 0.0, 1.0));
    worldPosition = instMul(model, vec4(a_position, 1.0)).xyz;
  #else
    worldPosition = mul(World, vec4(a_position, 1.0)).xyz;
  #endif

  vec4 position = jitterVertexPosition(worldPosition);

    vec4 texcoords;
    int frameIndex = int(a_color0.w * 255.0);
    texcoords.xy = (texcoord0 * BannerUVOffsetsAndScales[frameIndex].zw) + BannerUVOffsetsAndScales[frameIndex].xy;
    texcoords.zw = (texcoord0 * BannerUVOffsetsAndScales[0].zw) + BannerUVOffsetsAndScales[0].xy;

    float lightIntensity = calculateLightIntensity(World, vec4(a_normal.xyz, 0.0), TileLightColor);
    lightIntensity += OverlayColor.a * 0.35;
    vec4 light = vec4(lightIntensity * TileLightColor.rgb, 1.0);
    float grayscale = dot(light.rgb,vec3(0.2126, 0.7152, 0.0722));
    light.rgb = vec3(grayscale,grayscale,grayscale);

    float cameraDepth = position.z;
    float fogIntensity = calculateFogIntensity(cameraDepth, FogControl.z, FogControl.x, FogControl.y);
    vec4 fog = vec4(FogColor.rgb, fogIntensity);

    vec4 color;
#if !ALPHA_TEST && !DEPTH_ONLY_OPAQUE && TINTING
	color = BannerColors[frameIndex];
	color.a = 1.0;
	if (frameIndex > 0) {
	    color.a = 0.0;
	}
#else
    color = a_color0;
#endif

#if DEPTH_ONLY
    v_texcoord0 = vec2(0.0, 0.0);
    v_color0 = vec4(0.0, 0.0, 0.0, 0.0);
#else
    v_texcoord0 = texcoord0;
    v_color0 = color;
#endif

#if ALPHA_TEST || DEPTH_ONLY_OPAQUE
    v_texcoords = vec4(0.0, 0.0, 0.0, 0.0);
#else
    v_texcoords = texcoords;
#endif

    v_fog = fog;
    v_light = light;
  gl_Position = position;
}
