package annotation;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

public enum Etiquettes 
{

	/** énumération des étiquettes disponibles pour l'annotation manuelle et automatique*/
	 
	/**
	* hyperonyme et hyponyme	
	*/
	PLAN_ONTOLOGIQUE("Po" ,"plan ontologique"),
	PLAN_LANGAGIER("Pl", "plan langagier"),
	PROCEDE_INTRODUCTION("PRi" ,"procédé introduction"),
	PROCEDE_AJUSTEMENT("PRa" ,"procédé ajustement"),
	PROCEDE_REJET("PRr" ,"procédé rejet"),
	ATTITUDE_INTERACTION("Ai" ,"attitude interaction"),
	ATTITUDE_CADRAGE("Ac" ,"attitude cadrage"),
	ATTITUDE_PRISE_EN_CHARGE("Apc" ,"attitude prise en charge"),
	PLAN_INDETERMINE("Ind","plan indéterminé"),
	TYPE_ENTITE_HUMAINE("Eh","type entité humaine"),
	TYPE_OBJET_PHYSIQUE("Op","type objet physique"),
	TYPE_ENTITE_ABSTRAITE("Ea","type entité abstraite"),
	TYPE_PROCESSUS("Proc","type processus"),
	TYPE_EVENEMENT("Ev","type événement"),
	TYPE_LIEU("L","type lieu"),
	RELATION_ASSOCIATION("association", "relations r_association"),
	RELATION_TEMPORELLE("temporelle", "relations r_temporelle"),
	RELATION_SPATIALE("spatiale", "relations r_spatiale"),
	RELATION_CAUSALE("causale", "relations r_causale"),
	RELATION_IDENTITE("identité", "relations rs_identite"),
	RELATION_APPARTENANCE("causale", "relations rs_appartenance"),
	RELATION_INCLUSION("causale", "relations rs_inclusion"),
	RELATION_PARTIE_TOUT("partie-tout", "relations rs_partie_tout"),
	RELATION_LOCALISATION("localisation", "relations rs_localisation"),
	RELATION_RUPTION("ruption", "relations rs_ruption");

	//r_association","r_temporelle","r_spatiale","r_causale","rs_identite", "rs_appartenance", "rs_inclusion", "rs_partie_tout", "rs_localisation", "rs_ruption
//"entité humaine","objet physique (naturel ou artefact)","entité abstraite","processus","événement","lieu"
	/**
	* MEMBRES
	*/

	/**
	* Nom abrégé
	*/
	private String abbr;

	/**
	* Nom complet de l'étiquette
	*/
	private String typeName;

	/**
	* METHODES
	*/

	/**
	* Constructs a new <code>RelationType</code> with specified id and name.
	* 
	* @param id 
	* @param typeName (nom du type)
	*/
	private Etiquettes(String abbr, String typeName)
	{
		this.abbr      = abbr;
		this.typeName = typeName; 
	}
	public boolean hasName(String name)
	{
		boolean ok=false;
		for(Field f:Etiquettes.class.getDeclaredFields())
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
		for(Etiquettes t:Etiquettes.values())
		{
			String[] str=new String[2];
			str[0]=t.abbr;
			str[1]=t.typeName();
			set.add(str);
			System.out.println("processing "+t.abbr+" "+t.name());
		}
		return set;
	}
	/**
	* @return the identifier
	*/
		public String abbr ()
		{
			return this.abbr;
		}
		
		public static String valeur(String s)throws IllegalArgumentException
		{
			for(Etiquettes value:Etiquettes.values())
			{
				String v = value.typeName();
				if(s.equals(v))
		         { 
					String lab=value.abbr();
		            return lab; 
		         } 
		    }
			return null; 
		}
		/**
		* @return the name
		*/
		public String typeName()
		{
			return this.typeName;
		}
		
		public static void main(String[]args)
		{
			getAllTypes();
			System.out.println(valeur("procédé introduction"));
		}

}
