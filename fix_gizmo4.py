with open('src/main/java/com/stardew/craft/client/gui/DecorAnchorGizmoScreen.java', 'r') as f:
    text = f.read()
text = text.replace('mc.cameraEntity.getPosition(mc.getTimer().getGameTimeDeltaInternal()).toVector3f()', 'new Vector3f((float)mc.gameRenderer.getMainCamera().getPosition().x, (float)mc.gameRenderer.getMainCamera().getPosition().y, (float)mc.gameRenderer.getMainCamera().getPosition().z)')
with open('src/main/java/com/stardew/craft/client/gui/DecorAnchorGizmoScreen.java', 'w') as f:
    f.write(text)
