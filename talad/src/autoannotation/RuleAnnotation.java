package autoannotation;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
//import java.util.Map.Entry;
import java.util.Properties;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
/**
 * 
 * @author bebeshina
 * Classe pour l'annotation automatique à base de règles et sans sémantique 
 * Corpus annoté en POS et dépendances, 
 * pas de recours aux ressources de connaissance qui contiendraient l'information sur le sens des mots.
 */
public class RuleAnnotation 
{
	public RuleAnnotation()
	{
		
	}
	/**
	 * Fonction pour obtenir un sous-corpus centré sur un terme précis (tous les ségments qui concernent ce terme). 
	 * La sortie dépend de la façon dont le texte d'entrée à été segmenté.
	 * @param term terme autour duquel doit être construit le sous-corpus 
	 * @param path chemin vers le fichier qui contient le corpus de départ
	 * @param output chemin vers l'emplacement du sous-corpus qui sera obtenu pour le terme "term"
	 * @throws IOException
	 */
	public void subcorpus(String term, String path, String output) throws IOException
	{
		BufferedReader br=new BufferedReader(new FileReader(new File(path)));
		PrintWriter pw=new PrintWriter(new FileWriter(new File(output)));
		String l="";
		@SuppressWarnings("unused")
		int i=1;
		while((l=br.readLine())!=null)
		{
			if(l.contains(term))
			{
//				l=i+";"+l+";Po;MPR;PRa;MA;A";
				pw.println(l);
				pw.flush();
				i++;
			}
		}
		br.close();
		pw.close();
	}
	
	/**
	 *	Traitement de la ligne au format CONLL
	 * 	Stucture CONLL ID FORM LEMMA PLEMMA POS PPOS FEAT PFEAT HEAD PHEAD DEPREL PDEPREL
	 * 	ID (index in sentence, starting at 1)
		FORM (word form itself)
		LEMMA (word's lemma or stem)
		POS (part of speech)
		FEAT (list of morphological features separated by |)
		HEAD (index of syntactic parent, 0 for ROOT)
		DEPREL (syntactic relationship between HEAD and this word)
		PHEAD	Projective head (ignored by default, accessible through the special attribute @conll-x:phead).
		PDEPREL	The dependency relation for PHEAD (ignored by default, accessible through the special attribute @connl-x:pdeprel).
	 * @param cline ligne CONLL à traiter 
	 */
	public void conllLine(String cline)
	{
		String[]sline=cline.split("\t");
		System.out.println("nombre de colonnes = "+sline.length);
		
		for(int i=0;i<sline.length;i++)
		{
			switch(i)
			{
				case 0: 
					System.out.println("ID : "+sline[i]);
					break;
				case 1: 
					System.out.println("FORM : "+sline[i]);
					break;
				case 2: 
					System.out.println("LEMMA : "+sline[i]);
					break;
				case 3: 
					System.out.println("POS : "+sline[i]);
					break;
				case 4: 
					System.out.println("FEAT : "+sline[i]);
					break;
				case 5: 
					System.out.println("HEAD : "+sline[i]);
					break;
				case 6: 
					System.out.println("DEPREL : "+sline[i]);
					break;
				case 7: 
					System.out.println("PHEAD : "+sline[i]);
					break;
				case 8: 
					System.out.println("PDEPREL : "+sline[i]);
					break;
				case 9: 
					System.out.println("MISC : "+sline[i]);
					break;
					
			}
		}
	}
	public InputStream input;
	
	/**
	 * Lecture d'un fichier de texte (corpus étiqueté en POS/dépendances)
	 * @param f
	 * @return
	 * @throws IOException
	 */
	
	public String getFileContents(File f) throws IOException
	{
		String c="";
		byte[] encoded = Files.readAllBytes(Paths.get(f.getAbsolutePath()));
		c=new String(encoded, Charset.forName("UTF-8"));
		return c;
	}

	public static final String end="\n{2}";
	
	/**
	 * Obtention d'un flux de phrases étiquetées
	 * @param str
	 * @return
	 */
	public List<String> split(String str)
	{
	    return Stream.of(str.trim().split(end))
	      .map (elem -> new String(elem))
	      .collect(Collectors.toList());
	}
	
	public Sentence sent;

	public LinkedList<Map<String,String>>fl=new LinkedList<Map<String,String>>();
	
	public void execute(File f) throws IOException
	{
		String s=this.getFileContents(f);
		this.ruleInstance("$X>[T]>$Y", "patriotisme");
//		System.out.println("FILE CONTENTS : \n"+s);
		for(String l:this.split(s))
		{
//			System.out.println("SENTENCE : \n"+l);
			InputStream is=  new ByteArrayInputStream(l.getBytes());
			InputStreamReader isr=new InputStreamReader(is);
			sent=new Sentence(isr);
			
			/*matching règles*/
			this.matchRule();
		}
	}
	
	
	@SuppressWarnings("unused")
	private void getPropertyRules() throws IOException
	{
		String oprop=null;
		String propfile=readFileAsString("resources/regles.params");
		Date time = new Date(System.currentTimeMillis());
		String[] properties=propfile.split("(\n){2}");
		System.out.println("number of rules "+properties.length);
		StringBuilder sb=new StringBuilder();
		sb.append("");
		for(String p:properties)
		{
//			System.out.println("***"+p+"***");
//			this.getPropertyRules("$X~$Y~[T]~$Z?");
			this.input = new ByteArrayInputStream(p.getBytes(StandardCharsets.UTF_8.name()));
			Properties props=new Properties();
			props.load(input);
//			System.out.println(props);
			System.out.println("example "+props.getProperty("E"));
			System.out.println("structure "+props.getProperty("S"));
			System.out.println("conclusion "+props.getProperty("C"));
			for(Object pr:props.keySet())
			{
				if(pr.toString().contains("$"))
				{
					System.out.println("premisse "+props.getProperty(pr.toString()));
				}
			}	
		}
		System.out.println(sb.toString()+"\n"+"\nProgram ran on " + time);
	}
	/**
	 * TODO
	 * - matérialiser la phrase
	 * - parcourir la phrase à la recherche d'une structure S compte tenu de ses contraintes
	 * - si structure trouvée alors conclusion C
	 * - définir comment intégrer (de façon "légère") les contraintes sémantiques donc 
	 * 		>>> un premier passage sans ce type de contraintes 
	 * 		>>> puis analyse des erreurs et des instances (sémantique de $X, $Y, $Z)
	 * 		>>> définir les contraintes sémantiques indispensables  
	 */
	
	
	/**
	 * 
	 * @param structure
	 * @return
	 * @throws IOException
	 */
	public Map<String,String> getPropertyRules(String structure) throws IOException
	{
	/*différencier les différentes règles */
		
		String ruler=readFileAsString("resources/regles.params");
		
//		Date time = new Date(System.currentTimeMillis());
		String[] rules=ruler.split("(\\n){2}");
		StringBuilder sb=new StringBuilder();
		Map<String,String> map=new HashMap<String,String>();
		
		for(String rule:rules)
		{ 
//			System.out.println("RULE : "+rule);
			this.input = new ByteArrayInputStream(rule.getBytes(StandardCharsets.UTF_8.name()));
			Properties properties=new Properties();
			properties.load(input);
			String propName=properties.getProperty("S");
			if(propName.equals(structure))
			{
				String premisse1=properties.getProperty("$X");
				String premisse2=properties.getProperty("$Y");
				String premisse3=properties.getProperty("$Z");
				String conclusion=properties.getProperty("C");
				/**
				 * il existe au moins une prémisse pour chaque règle
				 */
				sb.append("structure "
						+structure+"\n"
						+	"premisse "	
						+	premisse1	
						+	"\n");
				map.put("$X", premisse1);
				/**
				 * il existe éventuellement d'autres prémisses
				 */
				if(premisse2!=null)
				{
					sb.append("premisse "	+	premisse2	+	"\n");
					map.put("$Y",premisse2);
				}
				if(premisse3!=null)
				{
					sb.append("premisse "	+	premisse3	+	"\n");
					map.put("$Z",premisse3);
				}
				sb.append("conclusion "	+	conclusion);
				
				map.put("C",conclusion);
				map.put("S", structure);
//				return map;
			}
			else 
			{
//				System.out.println("continue de chercher");
			}
		}
//		Date t = new Date(System.currentTimeMillis());
//		System.out.println(sb.toString()+"\n"+"\nProgram ran on " + t);
		
		return map;
	}
	
	public String[] getStructure(String structure)
	{
		System.out.println("recherche de la structure "+structure);
		String[] sr=structure.split("(\\s|\\>|\\~)");
		System.out.println("structure "+Arrays.asList(sr));
		return sr;
	}
	
	public String[][] inst;
	public Map<String,String> rmap;
	public Sentence instance(String structure, String term) throws IOException
	{
		Sentence sent=null;
//		Map<String,String> rmap=this.getPropertyRules(structure);
		rmap=this.getPropertyRules(structure);
//		String[]str=this.getStructure(structure);
		
		return sent;
	}

	public void ruleInstance(String structure, String term) throws IOException
	{
		int colonne;
//		Map<String,String> rmap=this.getPropertyRules(structure);
		rmap=this.getPropertyRules(structure);
//		for(Entry<String,String>e:rmap.entrySet())
//		{
//			System.out.println(e.getKey()+" "+e.getValue());
//		}
		String[]str=this.getStructure(structure);
		inst=new String[str.length][10];
//		
		for(int i=0;i<str.length;i++)
		{
//			System.out.println(rmap.size()+","+rmap.get(str[i]));
//			System.out.println("str[i]="+str[i]);
			if(str[i].equals("[T]"))
			{
				/*c'est le terme à rechercher*/
				colonne=1;
				inst[i][colonne]=term;
//				System.out.println("nomination "+term);
			}
			else
			{
//				System.out.println("***str[i]="+str[i]+" "+rmap.get(str[i]));
				Pattern p=Pattern.compile("\\$(X|Y|Z)");
				Matcher m=p.matcher(str[i]);
				if(m.find())
				{
					/*c'est un trait grammatical*/
					colonne=3;
					inst[i][colonne]=rmap.get(str[i]);
				}
				else
				{
					/*c'est du texte*/
					colonne=0;
					String forme=str[i].replaceAll("\\_", " ");
					inst[i][colonne]=forme;
					System.out.println("du texte "+forme);
				}
			}
		}
		for(int m=0;m<inst.length;m++)
		{
//			if(inst[m]==null)
//			{
//				
//			}
			System.out.println(Arrays.asList(inst[m]));
		}
		
	}
	public void applyAllRules(String s)
	{
		
	}
	@SuppressWarnings("unused")
	public void matchRule()
	{
		/*lire la phrase dans un tableau*/
		String start="";
		for(int i=0;i<10;i++)
		{
			if(inst[0][i]!=null)
			{
				start=inst[0][i];
			}
		}
//		System.out.println("START "+start);
		/**
		 * faire des ngrammes
		 */
//		StringBuilder sb=new StringBuilder();
//		System.out.println("sentence h = "+sent.h);
		for(int j=0;j<sent.h;j++)
		{
//				System.out.println("processing "+Arrays.deepToString(sent.tab[j]));
//				System.out.println(sent.w);
			for(int i=0;i<sent.w;i++)
			{
////				System.out.println("in the loop");
//					System.out.println("i,j "+i+","+j);
//					System.out.println(i+","+sent.tab[j][i]);
				
				if(sent.tab[j][i].contentEquals(start))
				{
//					System.out.println("sent "+j+","+i+" "+ sent.tab[j][i]);
//					for(int n=j;n<inst.length;n++)
					Integer match=0;
					String mstr="";
					for(int n=0;n<inst.length;n++)
					{
						if(n+j<sent.h)
						{
							for(int m=0;m<10;m++)
							{
								if(sent.tab[j+n][m].equals(inst[n][m]))
								{
									mstr=mstr+" "+sent.tab[j+n][1];
									match++;
								}
							}
						}
					}
					if (match==inst.length)
					{
						String lsent=sent.getSentence();
						
						System.out.println(lsent);
						System.out.println("match "+mstr+ " "+rmap.get("C"));
						Integer[]coord=new Integer[2];
						/*set avec les coordonnées*/
						int beg=0;
						int end=0;
						lsent.lastIndexOf(mstr, end);
						beg=lsent.indexOf(mstr);
						end=beg+mstr.length();
						
						
						
						System.out.println(beg+","+ end);
					}
				}
				
					
//					if(sent.tab[j][i].equals(start))
//					{
						
//						System.out.println("inst "+n+","+i+" "+  inst[n][i]);
//					}
//					System.out.println("i,j,n "+i+","+j+","+n);
//					if(sent.tab[n][j].equals(start))
//					{
//////							if(sent.tab[n+1][i].contentEquals(inst[n+1][i]))
//////							{
//////								sb.append(Arrays.deepToString(sent.tab[j])+"\n");
//							System.out.println("found start "+sent.tab[n][j]);
//////							}
//					}	
				
			}
		}
//		}
//		System.out.println(sb.toString());
	}

	/**
	 * utility functions
	 */
	@SuppressWarnings("resource")
	public static String readFileAsString(String file) throws FileNotFoundException 
	{
			Locale loc=new Locale("fr","FR");
		   return new Scanner(new FileInputStream(file),"UTF-8").useDelimiter("\\Z").useLocale(loc).next();
	}
	
	/**
	 * TODO
	 * règles ont une forme:
	 * P prémisse 
	 * P prémisse
	 * C conclusion
	 * et portent sur le segment annoté (entre sauts de ligne)
	 */
	
	/**
	 * 
	 * @param args
	 * @throws IOException
	 */
	
	
	public static void main (String[] args) throws IOException
	{
		RuleAnnotation ra=new RuleAnnotation();
//		ra.ruleInstance("$X>$Y>de>[T]", "protectionnisme");
//		ra.sub/bebeshina/talad/tnr/input/protectionnisme.txt.outmaltcorpus("protectionnisme", "./output/subcorpus/sub_protectionnisme.txt", "./output/subcorpus/tiny_protectionnisme.txt");
//		ra.conllLine("1	respecter	respecter	V	VINF	m=inf	1100000	41	obj	_	_");
//		ra.getPropertyRules("$X>$Y>de>[T]");
//		File f=new File("input/test_ra.txt");
		File f=new File("input/protectionnisme.txt.outmalt");
		ra.execute(f);
	}

}
