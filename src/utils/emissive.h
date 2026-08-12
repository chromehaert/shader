#ifndef EMISSIVE_H
#define EMISSIVE_H

//   ~252/255 (0.988-0.989)
vec3 EmissiveDetect(vec4 diffuse) {
  if (diffuse.a > 0.988 && diffuse.a < 0.993) {
    vec3 glow = diffuse.rgb * diffuse.rgb;
    return glow;
  }
  return vec3(0.0, 0.0, 0.0);
}

#endif
