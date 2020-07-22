package annotation;

import java.awt.Font;
import java.util.Vector;

import static org.junit.Assert.assertTrue;

import java.awt.Component;

public enum Polices 
{
	titre("Arial", Font.BOLD, 14),
	soustitre("Arial", Font.BOLD, 14),
	texte("Arial", Font.PLAIN, 14),
	intitule("Arial", Font.BOLD, 12),
	log("Arial",Font.PLAIN,10);
	
	/* membres */
	
	private Polices(String fontName, Integer fontConstant, Integer fontSize)
	{
		this.fontName=fontName;
		this.fontStyle=fontConstant;
		this.fontSize=fontSize;
	}
	
	
	
	private String fontName;
	private Integer fontSize;
	private Integer fontStyle;
	public String fontName()
	{
		return this.fontName;
	}
	public int fontConstant()
	{
		return this.fontStyle;
	}
	public int fontSize()
	{
		return this.fontSize;
	}
	public static Vector<Object> featFont(Polices s) //Vector<Object>
	{
		Vector<Object> vec=new Vector<Object>();
		for(Polices p:Polices.values())
		{
			System.out.println(p);
			if(p.equals(s))
			{
				vec.add(0, p.fontName());
				vec.add(1,p.fontConstant());
				vec.add(2,p.fontSize());
			}
		}
		System.out.println(vec);
		return vec;
	
	}
	public Component setFont(Component o,Polices p)
	{
		assertTrue(o instanceof Component);
		Font f=new Font(p.fontName,p.fontStyle,p.fontSize);
		o.setFont(f);
		
		return o;
	}
	
	public  Font getFont()
	{
		
		Font f=new Font(this.fontName,this.fontStyle,this.fontSize);
		return f;
	}
	public static void main(String[]args)
	{
		featFont(titre);
	}

	

}
