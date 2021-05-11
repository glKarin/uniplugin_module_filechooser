package com.pengniaoyun.uniplugin_module_filechooser.utility;

public final class VarRef<T>
{
	public T ref;

	public VarRef(){}
	public VarRef(T t)
	{
		ref = t;
	}

	public T Ref(T t)
	{
	    T old = ref;
	    ref = t;
	    return old;
	}

    public T Unref()
    {
        T old = ref;
        ref = null;
        return old;
    }

	public T Move(VarRef<T> other)
	{
		T old = ref;
		ref = null;
		other.Ref(old);
		return old;
	}

	public T Ref()
	{
		return ref;
	}

	public T Ref_T()
	{
		try
		{
			return (T)ref;
		}
		catch (Exception e)
		{
			Logf.DumpException(e);
			return null;
		}
	}
}
