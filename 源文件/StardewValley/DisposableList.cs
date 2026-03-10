using System;
using System.Collections.Generic;

namespace StardewValley;

[Obsolete("This is only kept for backwards compatibility. It should no longer be used, and no longer does anything besides wrap the provided list.")]
public struct DisposableList<T>(List<T> list)
{
	public struct Enumerator(DisposableList<T> parent) : IDisposable
	{
		private readonly DisposableList<T> _parent = parent;

		private int _index = 0;

		public T Current
		{
			get
			{
				if (_parent._list == null || _index == 0)
				{
					throw new InvalidOperationException();
				}
				return _parent._list[_index - 1];
			}
		}

		public bool MoveNext()
		{
			_index++;
			if (_parent._list != null)
			{
				return _parent._list.Count >= _index;
			}
			return false;
		}

		public void Reset()
		{
			_index = 0;
		}

		public void Dispose()
		{
		}
	}

	private readonly List<T> _list = list;

	public Enumerator GetEnumerator()
	{
		return new Enumerator(this);
	}
}
