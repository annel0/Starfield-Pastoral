package com.stardew.craft.client.weapon.animation;

public record WeaponSkillPose(float rx, float ry, float rz, float tx, float ty, float tz) {

    public static WeaponSkillPose lerp(WeaponSkillPose a, WeaponSkillPose b, float t) {
        return new WeaponSkillPose(
                lerp(a.rx, b.rx, t),
                lerp(a.ry, b.ry, t),
                lerp(a.rz, b.rz, t),
                lerp(a.tx, b.tx, t),
                lerp(a.ty, b.ty, t),
                lerp(a.tz, b.tz, t)
        );
    }

    public static WeaponSkillPose mirrorRightToLeft(WeaponSkillPose rightHand) {
        return new WeaponSkillPose(
                rightHand.rx,
                -rightHand.ry,
                -rightHand.rz,
                -rightHand.tx,
                rightHand.ty,
                rightHand.tz
        );
    }

    private static float lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }
}
