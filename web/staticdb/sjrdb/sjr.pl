use strict;

print <<ENDheadr;
PRAGMA synchronous = OFF;
PRAGMA journal_mode = OFF;
PRAGMA locking_mode = EXCLUSIVE;
PRAGMA automatic_index = FALSE;
PRAGMA cache_size = 20000;
CREATE TABLE sjrtbl(rank INTEGER PRIMARY KEY ASC, title TEXT, type TEXT, issn TEXT, sjr REAL);
BEGIN;
ENDheadr

my $lin;
my @col;
my $i;
my $row = 1;

open(FIL, "scimagojr.csv");
$lin = <FIL>; # read out first line

while (<FIL>)
{

chomp;
$lin = $_;
@col = split /\t/, $lin;
$col[3] =~ s/ISSN //;
$col[1] =~ s/"//g;
$col[1] =~ s/'/''/g;
print "INSERT INTO sjrtbl VALUES ($col[0], '$col[1]', '$col[2]', '$col[3]', $col[4]);\n";

}


close(FIL);

print <<ENDfooter;
COMMIT;
CREATE INDEX SJRtitleIdx ON sjrtbl (title COLLATE NOCASE);
CREATE INDEX SJRissnIdx ON sjrtbl (issn);
CREATE INDEX SJRrankIdx ON sjrtbl (rank);

ENDfooter

