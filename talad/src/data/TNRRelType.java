package data;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

public enum TNRRelType 
{

/**
* Enumération des types des relations utilisées dans le cadre de la terminologie de la nomination et de la référence (TNR).
*/
// fields ---------------------------------------------------------------
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
r_isa(1, "r_isa","SEM"),
r_hypo(2, "r_hypo","SEM"),

/**
* variantes, synonymes et termes similaires (sans être des synonymes stricts)
*/
r_variante(3,"r_variante","LEX/TERMINO"),
r_syn(4,"r_syn","LEX/TERMINO"),
r_similar(5,"r_similar","LEX"),

/**
* raffinement de sens (sens d'usage)
*/
r_raff_sem(6,"r_raff_sem","SEM"),

/**
* définitions et exemples
*/
r_definition(7, "r_definition","TERMINO"),
r_exemple(8,"r_exemple","TERMINO"),

/**
* domaine de spécialité dans lequel le terme est utilisé
*/
r_domaine (9, "r_domaine","SEM/TERMINO"),

/**
* relations partie-tout
*/
r_has_part(10,"r_has_part","SEM"),
r_holo(11,"r_holo","SEM"),

/**
* caractéristiques, manières, propriétés typiques possibles
*/
r_carac (12, "r_carac","SEM"),
r_prop(13,"r_prop","SEM"),
r_manner (14, "r_manner","SEM"),

/**
* relations actantielles : agent, patient, lieu, instrument d'une action, rôle télique et implication agentive 
*/
r_agent(15,"r_agent","SEM"),
r_patient(17,"r_patient","SEM"),
r_instr(18,"r_instr","SEM"),
r_lieu(19,"r_lieu","SEM"),
r_telic_role(20,"r_telic_role","SEM"),
r_agentive_implication(21,"r_implique","SEM"),

/**
* relations temporelles (éventuellement utiles) 
*/
r_avant (22, "r_avant","SEM"),
r_apres(23,"r_apres","SEM"),

/**
* cause et conséquence
*/
r_cause(24,"r_cause","SEM"),
r_conseq(25,"r_conseq","SEM"),

/**
* relations grammaticales (utilitaires)
*/
r_pos (26, "r_pos","GRAM"),
r_lemma (27, "r_lemma","GRAM"),
r_forme(28,"r_forme","GRAM"),

/**
*  relations nécessaires pour mettre en place les relations annotées
*/
r_annotation(29,"r_annotation","GRAPH"),
r_source(30,"r_source","GRAPH"),
r_target(31,"r_target","GRAPH"),

/**
* relation vers un mot composé 
*/
r_locution(32,"r_locution","LEX"),
r_associated(33,"r_associated","LEX/SEM"),
r_pattern(34,"r_pattern","GRAPH"),
r_frame(35,"r_frame","LEX/SEM"),
r_sdef(36,"r_sdef","LEX/SEM");

/**
* MEMBRES
*/

/**
* Identifier.
*/
private int id;

/**
* Type name.
*/
private String typeName;
/**
* type of the relationship, nature of the corresponding lexical function
*/
private String constraint;

/**
* METHODES
*/

/**
* Constructs a new <code>RelationType</code> with specified id and name.
* 
* @param id 
* @param typeName nom du type
*/
private TNRRelType(int id, String typeName,String constraint)
{
this.id       = id;
this.typeName = typeName; 
this.constraint=constraint;
}
public boolean hasName(String name)
{
boolean ok=false;
for(Field f:TNRRelType.class.getDeclaredFields())
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
for(TNRRelType t:TNRRelType.values())
{
String[] str=new String[3];
str[0]=String.valueOf(t.id);
str[1]=t.name();
str[2]=t.constraintType();
set.add(str);
//	System.out.println("processing "+t.id+" "+t.name()+" "+t.constraintType());
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
