$input a_color0, a_position
#ifdef INSTANCING
  $input i_data0, i_data1, i_data2, i_data3
#endif
$output v_color0

#include <bgfx_shader.sh>

uniform vec4 CloudColor;
uniform vec4 FogColor;
uniform vec4 DistanceControl;

void main() {
  #ifdef INSTANCING
    mat4 model = mtxFromCols(i_data0, i_data1, i_data2, i_data3);
  #else
    mat4 model = u_model[0];
  #endif

  vec3 worldPos = mul(model, vec4(a_position, 1.0)).xyz;

  vec4 color = a_color0 * CloudColor;

  float fogFade = clamp(1.0 - max((length(worldPos) / (DistanceControl.x * 1.7)) - 0.9, 0.0), 0.0, 1.0);
  color.a = fogFade*0.9;

  v_color0 = color;

  gl_Position = mul(u_viewProj, vec4(worldPos, 1.0));
}
