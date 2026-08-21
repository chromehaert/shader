#ifndef FOG_UTIL_H
#define FOG_UTIL_H

float calculateFog(float relativeDepth, vec4 fogDistCtrl) {
  return clamp((((relativeDepth / fogDistCtrl.z) - (0.05 + fogDistCtrl.x)) / (0.05 + (fogDistCtrl.y - fogDistCtrl.x))), 0.0, 1.0);
}

#endif // FOG_UTIL_H
