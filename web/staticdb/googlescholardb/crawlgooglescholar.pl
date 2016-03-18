use strict;
use utf8;
use LWP::Simple;
use URI::Escape;
use Text::Unidecode;
use HTML::Entities;
use String::Similarity;

my $lin;
my @col;
my $i;
my $row = 1;
my $elem;
my @parts;
my $encodedurl;
my $num = 1;
my $lastnum = 1;
my $h5index;
my $pub;
my $pub1;
my $ori;
my $orisimi;
my $pubsimi;
my $simi;
my $simistr;
my $run=0;

my %journals;
my @journlist;
my $contents;

open(FIL1, "../impactfactordb/impactfactor2014.csv");
while (<FIL1>)
{

chomp;
$lin = $_;
@col = split /\t/, $lin;
$col[1] =~ s/"//g;
$col[1] =~ s/'/''/g;
if (length($col[1]) == 0) {next;}
$journals{lc($col[1])} = "$col[1]\t$col[2]\n";

}
close(FIL1);

open(FIL2, "../sjrdb/scimagojr.csv");
$lin = <FIL2>; # read out first line

while (<FIL2>)
{

chomp;
$lin = $_;
@col = split /\t/, $lin;
$col[3] =~ s/ISSN //;
$col[1] =~ s/"//g;
$col[1] =~ s/'/''/g;
if (length($col[1]) == 0) {next;}
$journals{lc($col[1])} = "$col[1]\t$col[3]\n";
}
close(FIL2);

my $totalToSearch = scalar(keys(%journals));

open(FILa, "googlescholar_notfound.txt");
while(<FILa>)
{
	chomp;
	delete $journals{lc($_)};
	$run++;
}
close(FILa);

open(FILb, "googlescholar.csv");
while(<FILb>)
{
	chomp;
	@parts = split /\t/;
	if (scalar(@parts) < 4) {next;}
	$num = $parts[0] + 1;
	$lastnum = $num;
	delete $journals{lc($parts[1])};
	$run++;
}
close(FILb);

@journlist = values(%journals);

open(FILO,  ">>googlescholar.csv");
open(FILO2, ">googlescholar_crawl.log");
open(FILO3, ">>googlescholar_notfound.txt");
open(FILO4, ">googlescholar_similar.txt");
foreach $elem(@journlist)
{
chomp $elem;
$run++;
@parts = split /\t/, $elem;
$encodedurl = uri_escape_utf8($parts[0]);
$contents = get("http://scholar.google.com.my/citations?hl=en&view_op=search_venues&vq=".$encodedurl);

while ($contents =~ /<td class="gs_title">([^><]*)<\/td><td class="gs_num"><a href="[^\"]*">([0-9\.]+)<\/a><\/td>/gi)
{
	$h5index = $2;
	$pub = $1;
	$pub1 = unidecode(decode_entities($pub));
	$ori = $parts[0];
	$orisimi = simiconv($ori);
	$pubsimi = simiconv($pub);
	$simi = similarity $orisimi, $pubsimi;
	$simistr = sprintf("%.2f", $simi);
	$pub =~ s/\(//g;
	$ori =~ s/\(//g;
	$pub =~ s/\)//g;
	$ori =~ s/\)//g;
	if (($ori !~ /$pub/i) && ($pub !~ /$ori/i)) {$ori = decode_entities($ori);}
	if (($ori !~ /$pub/i) && ($pub !~ /$ori/i)) {$pub = decode_entities($pub);}
	if (($ori !~ /$pub/i) && ($pub !~ /$ori/i)) {$pub = unidecode($pub);}
	if (($ori !~ /$pub/i) && ($pub !~ /$ori/i)) {$pub =~ s/= / = /g;}
	if (($ori !~ /$pub/i) && ($pub !~ /$ori/i)) {$pub =~ s/Anesthesi/Anaesthesi/g;}
	if (($ori !~ /$pub/i) && ($pub !~ /$ori/i)) {$pub =~ s/Pediatric/Paediatric/g;}
	if (($ori !~ /$pub/i) && ($pub !~ /$ori/i)) {$pub =~ s/Reviews/Review/g;}
	if (($ori !~ /$pub/i) && ($pub !~ /$ori/i)) {$pub =~ s/Telecommunications/Telecommunication/g;}
	if (($ori !~ /$pub/i) && ($pub !~ /$ori/i)) {$pub =~ s/Plants/Plant/g;}
	if (($ori !~ /$pub/i) && ($pub !~ /$ori/i)) {$pub =~ s/fuer/fur/g;}
	if (($ori !~ /$pub/i) && ($pub !~ /$ori/i)) {$pub =~ s/ Symposium on / /g;}
	if (($ori !~ /$pub/i) && ($pub !~ /$ori/i)) {$pub =~ s/ of the / /g;}
	if (($ori !~ /$pub/i) && ($pub !~ /$ori/i)) {$pub =~ s/([;'\.]*)//g;}
	if (($ori !~ /$pub/i) && ($pub !~ /$ori/i)) {$pub =~ s/-/ - /g;}
	if (($ori !~ /$pub/i) && ($pub !~ /$ori/i)) {$pub =~ s/ - / /g;}
	if (($ori !~ /$pub/i) && ($pub !~ /$ori/i)) {$pub =~ s/: /-/g;}
	if (($ori !~ /$pub/i) && ($pub !~ /$ori/i)) {$pub =~ s/The //gi;}
	if (($ori !~ /$pub/i) && ($pub !~ /$ori/i)) {$pub =~ s/The//gi;}
	if (($ori !~ /$pub/i) && ($pub !~ /$ori/i)) {$pub =~ s/, and/ and/g;}
	if (($ori !~ /$pub/i) && ($pub !~ /$ori/i)) {$pub =~ s/ and/, and/g;}
	if (($ori !~ /$pub/i) && ($pub !~ /$ori/i)) {$pub =~ s/\//-/g;}
	if (($ori !~ /$pub/i) && ($pub !~ /$ori/i)) {$pub =~ s/([,]*)//g;}
	if (($ori !~ /$pub/i) && ($pub !~ /$ori/i)) {$pub =~ s/\&/and/g;}
	if (($ori !~ /$pub/i) && ($pub !~ /$ori/i)) {$pub =~ s/and/&/g;}
	if (($ori !~ /$pub/i) && ($pub !~ /$ori/i)) {$pub =~ s/ //g;}
	if (($ori !~ /$pub/i) && ($pub !~ /$ori/i)) {$ori =~ s/([;'\.]*)//g;}
	if (($ori !~ /$pub/i) && ($pub !~ /$ori/i)) {$ori =~ s/-/ - /g;}
	if (($ori !~ /$pub/i) && ($pub !~ /$ori/i)) {$ori =~ s/ - / /g;}
	if (($ori !~ /$pub/i) && ($pub !~ /$ori/i)) {$ori =~ s/:/ /g;}
	if (($ori !~ /$pub/i) && ($pub !~ /$ori/i)) {$ori =~ s/\//-/g;}
	if (($ori !~ /$pub/i) && ($pub !~ /$ori/i)) {$ori =~ s/([,]*)//g;}
	if (($ori !~ /$pub/i) && ($pub !~ /$ori/i)) {$ori =~ s/ //g;}
	if (($ori !~ /$pub/i) && ($pub !~ /$ori/i))
	{
		if ($simi < 0.95) {next;}
		else
		{
			print "*SIMI $orisimi, $pubsimi, $simi\n";
			print FILO2 "*SIMI $orisimi, $pubsimi, $simi\n";
		}
	}
	print "$num\t$parts[0]\t$parts[1]\t$h5index\n";
	print FILO "$num\t$parts[0]\t$parts[1]\t$h5index\n";
	$num++;
	last;
}

if ($lastnum == $num)
{
	if ($contents =~ /Your search - <strong>([^><]*)<\/strong> - didn't match any publications./)
	{
		print "Your search - $1 - didn't match any publications ($run out of $totalToSearch)\n";
		print FILO2 "Your search - $1 - didn't match any publications ($run out of $totalToSearch)\n";
		print FILO3 "$parts[0]\n";
	}
	elsif ($contents =~ /<table(.*)<\/table>/)
	{
		print "\n==> $parts[0]   *SIMI=$simi\n$1  ($run out of $totalToSearch)\n";
		print FILO2 "\n==> $parts[0]\n$1  ($run out of $totalToSearch)\n";
		if ($simi > 0.75) {print FILO4 "$parts[0]\t$simistr\t$pub1\t$h5index\n";}
	}
	else
	{
		print "\nERROR5 $parts[0]\n";
		print "$contents\n";
		print FILO2 "\nERROR5 $parts[0]\n";
		print FILO2 "$contents\n";
	}
}
$lastnum = $num;
}
close FILO;
close FILO2;
close FILO3;
close FILO4;


###################################################
sub simiconv
{
my ($str) = @_;
	$str = lc(unidecode(decode_entities($str)));
	$str =~ s/the/ /g;
	$str =~ s/[\/\(\)-:,]/ /g;
	$str =~ s/[ ]+/ /g;
	$str =~ s/[ ]+$//g;
	$str =~ s/^[ ]+//g;
	return $str;
}


