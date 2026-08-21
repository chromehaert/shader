$input a_position, a_color0, a_texcoord0, a_indices, a_normal
#ifdef INSTANCING
  $input i_data0, i_data1, i_data2
#endif

$output v_color0, v_fog, v_light, v_texcoord0

#include <bgfx_shader.sh>
#include <MinecraftRenderer.Materials/DynamicUtil.dragonh>
#include <MinecraftRenderer.Materials/TAAUtil.dragonh>
#include <MinecraftRenderer.Materials/FogUtil.dragonh>

uniform vec4 FogControl;
uniform vec4 FogColor;
uniform vec4 OverlayColor;
uniform vec4 TileLightIntensity;
uniform vec4 TileLightColor;
uniform vec4 ViewPositionAndTime;
uniform vec4 RenderDistance;
uniform vec4 DimensionID;
uniform vec4 TimeOfDay;
uniform vec4 Day;
uniform vec4 CameraPosition;

void main() {
  mat4 World = u_model[0];
  vec2 uv0 = 2.0*a_texcoord0.xy;
  uv0 = fract(uv0) + ((floor(uv0)-0.5)/16384.0);
  vec3 wpos;

  #ifdef INSTANCING
    mat4 model = mtxFromCols(i_data0, i_data1, i_data2, vec4(0.0, 0.0, 0.0, 1.0));
    wpos = instMul(model, vec4(a_position, 1.0)).xyz;
  #else
    wpos = mul(World, vec4(a_position, 1.0)).xyz;
  #endif

  vec4 position = jitterVertexPosition(wpos);

  #if !(defined(DEPTH_ONLY) || defined(INSTANCING))
    float lightIntensity = calculateLightIntensity(World, vec4(a_normal.xyz, 0.0), TileLightColor);
    lightIntensity += OverlayColor.a * 0.35;
    vec4 light = vec4(lightIntensity * TileLightColor.rgb, 1.0);
    float grayscale = dot(light.rgb,vec3(0.2126, 0.7152, 0.0722));
    light.rgb = vec3(grayscale,grayscale,grayscale);

    float cameraDepth = position.z;
    float camDis = length(cameraDepth);
    vec4 fogColor;
    fogColor.a = clamp((((camDis / FogControl.z) - (0.25 + FogControl.x)) / (0.25 + (FogControl.y - FogControl.x))), 0.0, 1.0);
    fogColor = vec4(FogColor.rgb, smoothstep(0.0,1.0,fogColor.a));

    v_texcoord0 = uv0;
    v_color0 = a_color0;
    v_fog = fogColor;
    v_light = light;
  #endif

  gl_Position = position;
}
