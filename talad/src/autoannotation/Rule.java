package autoannotation;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
//import java.util.Vector;

public class Rule extends RuleAnnotation
{
	public String structure;
	public String variable;
	public boolean isVariable;
	public String premisse;
	public String cadre;
	
	
	/**
	 * instancier
	 * -lire structure
	 * -récupérer variables
	 */
	public Rule() {super();}
	
	boolean isVariable(String s)
	{
		boolean b=false;
		if(s.startsWith("$"))
		{
			b=true;
		}
		return b;
	}
	public String[] getStructure(String structure)
	{
		System.out.println("recherche de la structure "+structure);
		String[] sr=structure.split("(\\s|\\>|\\~)");
		System.out.println("structure "+Arrays.asList(sr));
		return sr;
	}
//	public String[] getPremisse()
//	{
//		
//		int colonne;
//		String s;
//		
//	}
	public void ruleInstance(String structure, String term) throws IOException
	{
		int colonne;
		Map<String,String> rmap=this.getPropertyRules(structure);
		String[]str=this.getStructure(structure);
		String[][]inst=new String[str.length][10];
		
		for(int i=0;i<str.length;i++)
		{
			System.out.println(rmap.size()+","+rmap.get(str[i]));
			if(rmap.get(str[i]).equals("[T]"))
			{
				/*c'est le terme à rechercher*/
				colonne=0;
				inst[i][colonne]=str[i];
				System.out.println("nomination "+rmap.get(str[i]));
			}
				
				
			if(rmap.get(str[i]).matches("[A-Z]+"))
			{
				/*c'est un trait grammatical*/
				colonne=2;
				inst[i][colonne]=rmap.get(str[i]);
				System.out.println("prémisse "+rmap.get(str[i]));
			}
				
		
			else
			{
				/*c'est du texte*/
				colonne=0;
				String forme=str[i].replaceAll("\\_", " ");
				inst[i][colonne]=forme;
				System.out.println("du texte "+rmap.get(str[i]));
			}
		}
	}
	public void execute() throws IOException
	{
		String test="$X>mot>[T]";
		this.ruleInstance(test,"protectionnisme");
	}
	public static void main(String[] args) throws IOException
	{
		Rule r=new Rule();
		r.execute();
	}
}
