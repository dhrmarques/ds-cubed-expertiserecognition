use strict;

print <<ENDheadr;
CREATE TABLE gsctbl(indexno INTEGER PRIMARY KEY ASC, title TEXT, issn TEXT, h5index REAL);
BEGIN;
ENDheadr

my $lin;
my @col;
my $i;
my $row = 1;

open(FIL, "googlescholar.csv");

while (<FIL>)
{

chomp;
$lin = $_;
@col = split /\t/, $lin;
if (scalar(@col) < 4) {next;}
$col[1] =~ s/"//g;
$col[1] =~ s/'/''/g;
print "INSERT INTO gsctbl VALUES ($col[0], '$col[1]', '$col[2]', $col[3]);\n";

}


close(FIL);

print <<ENDfooter;
COMMIT;
CREATE INDEX GSCtitleIdx ON gsctbl (title COLLATE NOCASE);
CREATE INDEX GSCissnIdx ON gsctbl (issn);
CREATE INDEX GSCindexnoIdx ON gsctbl (indexno);

ENDfooter

