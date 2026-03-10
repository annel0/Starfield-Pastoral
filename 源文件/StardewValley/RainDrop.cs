using Microsoft.Xna.Framework;

namespace StardewValley;

public struct RainDrop(int x, int y, int frame, int accumulator)
{
	public int frame = frame;

	public int accumulator = accumulator;

	public Vector2 position = new Vector2(x, y);
}
