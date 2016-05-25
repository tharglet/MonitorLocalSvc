package uk.ac.jisc.monitorlocal

import com.k_int.grails.tools.refdata.Defaults
import com.k_int.grails.tools.refdata.RefdataValue

class AoDeposit {
  static belongsTo = [ academicOutput: AcademicOutput ]
  static mappedBy = [
    academicOutput:'deposits',
  ]

  @Defaults(['Author\'s Original', 'Submitted Manuscript Under Review', 'Accepted Manuscript', 'Proof', 'Version of Record', 'Corrected Version of Record', 'Enhanced Version of Record', 'Not Applicable'])
  RefdataValue versionDeposited

  @Defaults(["Aberdeen University Research Archive","Abertay Research Collections","Aberystwyth University Repository","Anglia Ruskin Research Online",
	"Archaeology Data Service","arXiv.org e-Print Archive","Aston University Research Archive","BEAR (Buckingham E-Archive of Research)",
	"Birkbeck Institutional Research Online","Bournemouth University Research Online","Bradford Scholars","British History Online",
	"Brunel University Research Archive","Canterbury Research and Theses Environment","CEDA Repository","Central Archive at the University of Reading",
	"Central Lancashire Online Knowledge","ChesterRep","City Research Online","Cognitive Sciences ePrint Archive",
	"Computer Laboratory Technical Reports - Cambridge University","Cranfield CERES","Cronfa at Swansea University","CURVE (Coventry University)",
	"De Montfort University Open Research Archive","Kings College London (KCL) Department of Computer Science E-Repository",
	"University of Sheffield Department of Computer Science Publications Archive","Digital Education Resource Archive (Institute of Education)",
	"DSpace @ Cambridge","DSpace at Cardiff Met","Durham Research Online","e-Prints Soton (University of Southampton)",
	"e-space at Manchester Metropolitan University","eBangor","eCrystals - Southampton","Edge Hill Research Archive",
	"Edinburgh DataShare","Edinburgh Research Archive","Edinburgh Research Explorer","Electronics and Computer Science EPrints Service - University of Southampton",
	"EMBL-EBI's Protein Data Bank in Europe (PDBe)","Enlighten (University of Glasgow)","ePubs: the open archive for STFC research publications","ESC publication (University of Cambridge)",
	"ESRC Research Catalogue","Eureka (Saïd Business School, University of Oxford)","Europe PubMed Central (EPMC)","Explore Bristol Research (University of Bristol)",
	"Falmouth University Research Repository (FURR)","FigShare","Glamorgan Dspace","Glasgow DSpace Service",
	"Glasgow Theses Service","Glyndŵr University Research Online","Goldsmiths Research Online","Greenwich Academic Literature Archive",
	"Heythrop College Publications","IDS OpenDocs","Informatics@Edinburgh - Reports Series","Insight",
	"Institute of Cancer Research Repository","Institute of Education EPrints","NTU Institutional Repository (Nottingham Trent University)","Jisc Repository",
	"Keele Research Repository","Kent Academic Repository","King's Research Portal (Kings College London / KCL)","Kingston University Research Repository",
	"Lancaster EPrints","Leeds Beckett University Repository","Leicester Research Archive","LJMU Research Online (Liverpool John Moores University)",
	"London Met Repository","Loughborough University Institutional Repository","LSBU Research Open (London South Bank University)","LSE Research Online",
	"LSE Theses Online","LSHTM Data Compass","LSHTM Data Compass","LSHTM Research Online",
	"LSTM Online Archive","Manchester eScholar Services","Mathematical Institute Eprints Archive (University of Oxford)","Mathematics in Industry Information Service Eprints Archive (University of Oxford)",
	"Middlesex University Research Repository","MIMS EPrints (Manchester Institute for Mathematical Sciences)","Modern Languages Publications Archive (University of Nottingham)","Nature Precedings",
	"NECTAR (University of Northampton)","NERC Open Research Archive","Newcastle University E-Prints","Northumbria Research Link",
	"Nottingham ePrints","Nottingham eTheses","Online Repository of Birkbeck Institutional Theses","Online Research @ Cardiff",
	"Open Access Institutional Repository at Robert Gordon University","Open Research Exeter","ORO: Open Research Online (The Open University)","ORA: Oxford University Research Archive",
	"Parade@Portsmouth","Pharmacy Eprints (University College London / UCL)","PhilPapers (University of London)","Plymouth Electronic Archive and Research Library",
	"Plymouth Marine Science Electronic Archive (PlyMEA)","Portsmouth Research Portal","PubMed Central (PMC)","Queen Margaret University eResearch",
	"Queen Mary Research Online","Queen's Papers on Europeanisation (Queen's University, Belfast / QUB)","Queen's University Research Portal (Queen's University, Belfast / QUB)","RaY - Research at York St John",
	"Repository@Napier","Research Archive and Digital Asset Repository (Oxford Brookes University)","Research Art Design Architecture Repository (Glasgow School of Art)","Research at the University of Wales, Newport",
	"Research SPAce (Bath Spa University)","ResearchOnline@GCU (Glasgow Caledonian University)","ReStore repository (University of Southampton)","ROAR at University of East London",
	"Roehampton University Research Repository","Royal College of Art Research Repository","Royal Holloway Research Online","RVC Research Online (Royal Veterinary College)",
	"SAS-SPACE (School of Advanced Studies, University of London)","SCOAP3 Repository","ShareGeo Open","Sheffield Hallam University Research Archive",
	"Sheffield Hallam University Research Data Archive","SIRE (Scottish Institute for Research in Economics)","SOAS Research Online","Solent Electronic Archive",
	"Spiral - Imperial College Digital Repository","SRUC Repository (Scotlands Rural College)","St Andrews Research Repository","St George's Online Research Archive",
	"St Mary's University Open Research Archive","Stirling Online Repository for Research Data","Stirling Online Research Repository","STORE - Staffordshire Online Repository",
	"Strathprints","Sunderland University Institutional Repository","Surrey Research Insight","Sussex Research Online",
	"Sussex Research Online","Ted Nelson's EPrint Archive (University of Southampton)","Teeside University's Research Repository","The Research Output Service (Heriot-Watt University)",
	"UAL Research Online (University of the Arts London)","UCL Discovery (University College London)","UHI Research Repository (University of the Highlands and Islands)","Ulster Institutional Repository",
	"University of Bath Online Publication Store","University of Bedfordshire Repository","University of Birmingham Research Archive (E-papers)","University of Birmingham Research Archive (E-prints)",
	"University of Birmingham Research Archive (E-theses)","University of Bolton Institutional Repository","University of Brighton Repository","University of Chichester EPrints Repository",
	"University of Derby Online Research Archive","University of Dundee Online Publications","University of East Anglia digital repository","University of Essex Research Repository",
	"University of Gloucestershire, Research Repository","University of Hertfordshire Research Archive","University of Huddersfield Repository","University of Hull Institutional Repository",
	"University of Lincoln Institutional Repository","University of Liverpool Repository","University of Salford Institutional Repository","University of Strathclyde Institutional Repository",
	"University of Wales Trinity Saint David","University of Worcester Research and Publications","UWE Research Repository","UWL Repository (University of West London)",
	"Visual Arts Data Service","Warwick Research Archives Portal Repository","WestminsterResearch (University of Westminster)","White Rose E-theses Online",
	"White Rose Research Online","Wolverhampton Intellectual Repository and E-theses","York Digital Library"])
  RefdataValue name
  String url
  Date depositDate

  static constraints = {
    name nullable: true
    url nullable: true
    versionDeposited nullable: true
    depositDate nullable: true
  }

}
