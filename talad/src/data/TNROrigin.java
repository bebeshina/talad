package data;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

	public enum TNROrigin 
	{

	/**
	* Enumération des origines des relations présentes dans la base de donnés TNR
	/**
	* type de contrainte 
	* SEM	sémantique (relations sémantiques)
	* GRAM	grammaticale (relations qui lient les termes à des termes qui matérialisent 
	* - les catégories grammaticales ( POS);
	* - la forme canonique (lemme);
	* - relation lemme - forme. 
	* LEX	lexicale
	* - variantes ; 
	* - synonymes ;
	* - termes similaires ( sans être des synonymes stricts).
	* TERMINO	terminologique (relations nécessaires dans le cadre de spécification d'une terminologie) 
	GRAPH	relative à la représentation sous forme de graphe
	*/
	/**
	* hyperonyme et hyponyme	
	*/
	tnr(1, "tnr","IMPORT"), // import automatique depuis le glossaire TNR
	jdm(2, "jdm","IMPORT"), // import automatique depuis la base RezoJDM
	fn(3, "fn","IMPORT"), // import automatique depuis la base FrameNet
	wiki(4, "wiki","IMPORT"), // import automatique depuis Wikipedia

	/**
	* variantes, synonymes et termes similaires (sans être des synonymes stricts)
	*/
	man(5,"man","MANUEL"),// contribution manuelle via requete mysql unique
	nadia(6,"agata","INTERFACE"), // contribution via interface par Agata 
	agata(7,"nadia","INTERFACE"); // contribution via interface pat Nadia 

	/**
	* MEMBRES
	*/

	/**
	* Identifier.
	*/
	public int id;

	/**
	* Type name.
	*/
	public String typeName;
	/**
	* type of the relationship, nature of the corresponding lexical function
	*/
	public String constraint;

	/**
	* METHODES
	*/

	/**
	* Constructs a new <code>RelationType</code> with specified id and name.
	* 
	* @param id 
	* @param typeName nom du type
	*/
	private TNROrigin(int id, String typeName,String constraint)
	{
	this.id       = id;
	this.typeName = typeName; 
	this.constraint=constraint;
	}
	public boolean hasName(String name)
	{
		boolean ok=false;
		for(Field f:TNROrigin.class.getDeclaredFields())
		{
			System.out.println(f);
			if (f.toString().equals(name))
			{
				ok=true;
			}	
		}
		return ok;
	}
	/**
	* obtenir la liste de tous les types de relations définies dans ce projet
	* sous forme d'un ensemble de chaînes de caractères (Set<String>)
	*/

	public static Set<String[]> getAllTypes()
	{
		Set<String[]> set=new HashSet<String[]>();
		for(TNROrigin t:TNROrigin.values())
		{
			String[] str=new String[3];
			str[0]=String.valueOf(t.id);
			str[1]=t.name();
			str[2]=t.constraintType();
			set.add(str);
//		System.out.println("processing "+t.id+" "+t.name()+" "+t.constraintType());
		}
		return set;
	}
	/**
	* @return the identifier
	*/
	public int id()
	{
	return this.id;
	}

	/**
	* @return the name
	*/
	public String typeName()
	{
		return this.typeName;
	}
	public String constraintType()
	{
		return this.constraint;
	}
	public static void main(String[]args)
	{
		getAllTypes();
	}
	

}
