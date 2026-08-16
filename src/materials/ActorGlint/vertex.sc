$input a_position, a_color0, a_texcoord0, a_indices, a_normal
#ifdef INSTANCING
  $input i_data0, i_data1, i_data2
#endif

$output v_color0, v_fog, v_light, v_texcoord0, v_glintuv

#include <bgfx_shader.sh>
#include <MinecraftRenderer.Materials/DynamicUtil.dragonh>
#include <MinecraftRenderer.Materials/TAAUtil.dragonh>
#include <MinecraftRenderer.Materials/FogUtil.dragonh>
#include <MinecraftRenderer.Materials/GlintUtil.dragonh>

uniform vec4 OverlayColor;
uniform vec4 TileLightColor;
uniform vec4 FogColor;
uniform vec4 FogControl;
uniform vec4 UVAnimation;
uniform vec4 UVScale;
uniform mat4 Bones[8];
uniform vec4 ViewPositionAndTime;
uniform vec4 RenderDistance;
uniform vec4 DimensionID;
uniform vec4 TimeOfDay;
uniform vec4 Day;
uniform vec4 CameraPosition;

void main() {
  mat4 World = u_model[0];

  World = mul(World, Bones[int(a_indices)]);

  vec2 texcoord0 = a_texcoord0;

  vec3 worldPosition;
  #ifdef INSTANCING
    mat4 model = mtxFromCols(i_data0, i_data1, i_data2, vec4(0.0, 0.0, 0.0, 1.0));
    worldPosition = instMul(model, vec4(a_position, 1.0)).xyz;
  #else
    worldPosition = mul(World, vec4(a_position, 1.0)).xyz;
  #endif

  vec4 position = jitterVertexPosition(worldPosition);

  #if !(defined(DEPTH_ONLY_OPAQUE) || defined(DEPTH_ONLY) || defined(INSTANCING))

    float lightIntensity = calculateLightIntensity(World, vec4(a_normal.xyz, 0.0), TileLightColor);
    lightIntensity += OverlayColor.a * 0.35;
    vec3 grayscale = vec3(dot(TileLightColor.rgb,vec3(0.2126, 0.7152, 0.0722)));
    vec4 light = vec4(lightIntensity * grayscale, 1.0);

    float cameraDepth = position.z;
    float fogIntensity = calculateFogIntensity(cameraDepth, FogControl.z, FogControl.x, FogControl.y);
    vec4 fog = vec4(FogColor.rgb, fogIntensity);

    vec4 glintuv;
    glintuv.xy = calculateLayerUV(texcoord0, UVAnimation.x, UVAnimation.z, UVScale.xy);
    glintuv.zw = calculateLayerUV(texcoord0, UVAnimation.y, UVAnimation.w, UVScale.xy);

    v_texcoord0 = texcoord0;
    v_glintuv = glintuv;
    v_color0 = a_color0;
    v_fog = fog;
    v_light = light;
  #endif

  gl_Position = position;
}
