$input a_color0, a_position
#ifdef INSTANCING
  $input i_data0, i_data1, i_data2, i_data3
#endif
$output v_color0

#include <bgfx_shader.sh>
#include <utils/fog.h>

uniform vec4 FogColor;
uniform vec4 FogAndDistanceControl;
uniform vec4 ViewPositionAndTime;

void main() {
  #ifdef INSTANCING
    mat4 model = mtxFromCols(i_data0, i_data1, i_data2, i_data3);
  #else
    mat4 model = u_model[0];
  #endif

  vec3 worldPos = mul(model, vec4(a_position, 1.0)).xyz;

  float relativeDepth = length(ViewPositionAndTime.xyz - worldPos) / FogAndDistanceControl.w;
  float fogFade = calculateFogFade(relativeDepth, FogAndDistanceControl);

  vec4 color = a_color0;
  color.rgb = mix(color.rgb, FogColor.rgb, fogFade);

  v_color0 = color;

  gl_Position = mul(u_viewProj, vec4(worldPos, 1.0));
}
