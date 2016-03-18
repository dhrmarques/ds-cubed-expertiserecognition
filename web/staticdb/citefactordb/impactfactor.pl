use strict;

print <<ENDheadr;
CREATE TABLE iftbl(indexno INTEGER PRIMARY KEY ASC, title TEXT, issn TEXT, impactfactor REAL);
BEGIN;
ENDheadr

my $lin;
my @col;
my $i;
my $row = 1;

open(FIL, "impactfactor2014.csv");

while (<FIL>)
{

chomp;
$lin = $_;
@col = split /\t/, $lin;
$col[1] =~ s/"//g;
$col[1] =~ s/'/''/g;
print "INSERT INTO iftbl VALUES ($col[0], '$col[1]', '$col[2]', $col[3]);\n";

}


close(FIL);

print <<ENDfooter;
COMMIT;
CREATE INDEX IFtitleIdx ON iftbl (title COLLATE NOCASE);
CREATE INDEX IFissnIdx ON iftbl (issn);
CREATE INDEX IFindexnoIdx ON iftbl (indexno);

ENDfooter

