use strict;

print <<ENDheadr;
CREATE TABLE iftbl(indexno INTEGER PRIMARY KEY ASC, title TEXT, issn TEXT, impactfactor REAL, eigenfactor REAL);
BEGIN;
ENDheadr

my $lin;
my @col;
my $i;
my $row = 1;

open(FIL, "jcr-impact-factors-list-2013.csv");

while (<FIL>)
{

chomp;
$lin = $_;
@col = split /\t/, $lin;
$col[1] =~ s/"//g;
$col[1] =~ s/'/''/g;
$col[2] =~ s/-//g;
if (length($col[4]) == 0) {$col[4] = '0';}
if (length($col[9]) == 0) {$col[9] = '0';}
print "INSERT INTO iftbl VALUES ($col[0], '$col[1]', '$col[2]', $col[4], $col[9]);\n";

}


close(FIL);

print <<ENDfooter;
COMMIT;
CREATE INDEX IFtitleIdx ON iftbl (title COLLATE NOCASE);
CREATE INDEX IFissnIdx ON iftbl (issn);
CREATE INDEX IFindexnoIdx ON iftbl (indexno);

ENDfooter

