#ifndef INSTANCING
  $input a_color0, a_position
  $output v_color0
#endif

#include <bgfx_shader.sh>

#ifndef INSTANCING
  uniform vec4 SkyColor;
  uniform vec4 FogColor;
#endif

void main() {
  #ifndef INSTANCING
    vec4 pos = vec4(a_position.xyz, 1.0);
    
    float t = clamp((SkyColor.z - 0.5) / 0.5, 0.0, 1.0);
    vec4 colorMultiplier = vec4(mix(1.0, 1.35, t), 1.0, 1.0, 1.0);
    
    v_color0 = mix(SkyColor * colorMultiplier, FogColor, a_color0.x);

    gl_Position = mul(u_modelViewProj, pos);
  #else
    gl_Position = vec4(0.0, 0.0, 0.0, 0.0);
  #endif
}
