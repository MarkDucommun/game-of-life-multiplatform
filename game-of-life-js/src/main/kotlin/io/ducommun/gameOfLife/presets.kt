package io.ducommun.gameOfLife

actual object PresetStrings {
    actual val R_PENTOMINO: String by lazy {
       """#N R-pentomino
#C A methuselah with lifespan 1103.
#C www.conwaylife.com/wiki/index.php?title=R-pentomino
x = 3, y = 3, rule = B3/S23
b2o${'$'}2ob${'$'}bo!"""
    }
    actual val R_PENTOMINO_SYNTHESIS: String by lazy { TODO() }
    actual val ACORN: String by lazy {
        """#N Acorn
#O Charles Corderman
#C A methuselah with lifespan 5206.
#C www.conwaylife.com/wiki/index.php?title=Acorn
x = 7, y = 3, rule = B3/S23
bo5b${'$'}3bo3b${'$'}2o2b3o!"""
    }
    actual val BREEDER_ONE: String by lazy {
        """#N Breeder 1
#O Bill Gosper
#C The first pattern to be found that exhibits quadratic growth. Found
#C  in the early 1970s.
#C www.conwaylife.com/wiki/index.php?title=Breeder_1
x = 749, y = 338, rule = b3/s23
404bo2bo341b${'$'}408bo340b${'$'}404bo3bo340b${'$'}405b4o340b${'$'}416b2o331b${'$'}402bo11bo4bo
329b${'$'}400bobo17bo328b${'$'}342bobo46bo8bobo11bo5bo328b${'$'}342bobo44bo3bo21b6o5b
6o317b${'$'}331bo10bob2o48bo30bo5bo317b${'$'}329bo3bo10b2o43bo4bo36bo317b${'$'}334bo
6bo2bo45b5o30bo4bo318b${'$'}329bo4bo7b2o83b2o320b${'$'}330b5o50b2o362b${'$'}385b2o32b
3o5b2o320b${'$'}385b2o2bo13bo13bo3bo4bob2o319b${'$'}368b2o12b2ob2o3bo11bobo12bo
7b2obobo318b${'$'}355b2o10bo2bo8b2o2bobo4bo4b2o4bo9b2o4bo7bob2ob2o317b${'$'}355b
2o11b2o9b2o2b3o3bo5b2o5bo8b2o5bo2bo3bo3b2o318b${'$'}419bobo5b3o319b2${'$'}419bob
o5b3o319b${'$'}355b2o11b2o9b2o2b3o3bo5b2o5bo8b2o5bo2bo3bo3b2o318b${'$'}355b2o10b
o2bo8b2o2bobo4bo4b2o4bo9b2o4bo7bob2ob2o317b${'$'}368b2o12b2ob2o3bo11bobo12b
o7b2obobo318b${'$'}385b2o2bo13bo13bo3bo4bob2o319b${'$'}385b2o32b3o5b2o320b${'$'}330b
5o50b2o362b${'$'}329bo4bo7b2o83b2o320b${'$'}334bo6bo2bo45b5o30bo4bo318b${'$'}329bo3bo
10b2o43bo4bo36bo317b${'$'}331bo10bob2o48bo30bo5bo317b${'$'}342bobo44bo3bo21b6o5b
6o317b${'$'}342bobo46bo8bobo11bo5bo328b${'$'}400b2o18bo328b${'$'}401bo12bo4bo329b${'$'}
416b2o331b${'$'}477b2o270b${'$'}475b2ob2o269b${'$'}475b4o270b${'$'}476b2o271b${'$'}376bobo370b${'$'}
376b2o111b2o258b${'$'}377bo107b4ob2o5b4o248b${'$'}485b6o5b6o247b${'$'}463b2o21b4o6b4o
b2o246b${'$'}460b3ob2o34b2o247b${'$'}403b2o55b5o21bo262b${'$'}400b3ob2o46bo8b3o23bo
261b${'$'}352bobo45b5o21b3o23bo32b4o260b${'$'}352b2o47b3o20bo2b2o54b2o3bob2o257b
${'$'}353bo69bo3bobo26bo11b2o14bobobobo7b2o249b${'$'}421bo3bo9bobo13b4obo10bo2bo
13bo3b3o2bo3bo2b2o247b${'$'}405b2o14bo3b2o11bo11bo2bob2o4b2o4bobo7b2o6bobo
3bo4b3o2bo247b${'$'}405b2o14bo8bo3b2obo12bobo8b2o5bo8b2o7bo4b3o2bo4bo247b${'$'}
422bo3bo3b3o3bo14bo44b5o248b2${'$'}328bobo91bo3bo3b3o3bo14bo44b5o248b${'$'}328b
2o11b2o30b2o30b2o14bo8bo3b2obo12bobo8b2o5bo8b2o7bo4b3o2bo4bo247b${'$'}329bo
11b2o30b2o30b2o14bo3b2o11bo11bo2bob2o4b2o4bobo7b2o6bobo3bo4b3o2bo247b${'$'}
421bo3bo9bobo13b4obo10bo2bo13bo3b3o2bo3bo2b2o247b${'$'}423bo3bobo26bo11b2o
14bobobobo7b2o249b${'$'}424bo2b2o54b2o3bob2o257b${'$'}426b3o23bo32b4o260b${'$'}452bo
8b3o23bo261b${'$'}460b5o21bo262b${'$'}460b3ob2o34b2o89b2o156b${'$'}463b2o21b4o6b4ob2o
87b4o155b${'$'}485b6o5b6o88b2ob2o154b${'$'}485b4ob2o5b4o91b2o155b${'$'}489b2o258b${'$'}
464bo136b4o144b${'$'}464bobo133b6o143b${'$'}280bobo181b2o134b4ob2o142b${'$'}280b2o
294b3o13b3o9b2o9b2o132b${'$'}281bo252bobo38b5o12bo18b4ob2o131b${'$'}516b3o14b2o
2bo37b3ob2o12bo17b6o132b${'$'}515b5o12b3o2bo40b2o27bo4b4o133b${'$'}515b3ob2o10b
3o72bo2bo139b${'$'}440bo77b2o11bobo2b2o59b3o149b${'$'}440bobo87b2ob3obo38b2o17b
5o12b2o135b${'$'}440b2o89bo6bo37b2o16b3o14bo3bo133b${'$'}532bo4bo20b2o15bo2bo14b
o3bo9bo3bo4bo132b${'$'}534b5o6b2o10bo2bo8b2o4bobo7b2o5b2o2bo4b2o5bobo5bo
132b${'$'}538b2o5b2o11b2o9b2o5bo8b2o5bo6b3o9b2ob3o132b${'$'}538b2o53b8o148b2${'$'}
538b2o53b8o148b${'$'}449b2o30b2o30b2o23b2o5b2o11b2o9b2o5bo8b2o5bo6b3o9b2ob
3o81b2o49b${'$'}232bobo214b2o30b2o30b2o19b5o6b2o10bo2bo8b2o4bobo7b2o5b2o2bo
4b2o5bobo5bo79b2ob2o48b${'$'}232b2o298bo4bo20b2o15bo2bo14bo3bo9bo3bo4bo79b
4o49b${'$'}233bo297bo6bo37b2o16b3o14bo3bo81b2o50b${'$'}530b2ob3obo38b2o17b5o12b
2o135b${'$'}531bobo2b2o59b3o110b2o37b${'$'}531b3o72bo2bo96b4ob2o5b4o27b${'$'}392bo
139b3o2bo40b2o27bo4b4o90b6o5b6o26b${'$'}392bobo138b2o2bo37b3ob2o12bo17b6o
67b2o21b4o6b4ob2o25b${'$'}392b2o140bobo38b5o12bo18b4ob2o63b3ob2o34b2o26b${'$'}
576b3o13b3o9b2o9b2o7b2o55b5o21bo41b${'$'}600b4ob2o14b3ob2o46bo8b3o23bo40b${'$'}
600b6o15b5o21b3o23bo32b4o39b${'$'}601b4o17b3o20bo2b2o54b2o3bob2o36b${'$'}644bo3b
obo26bo11b2o14bobobobo7b2o28b${'$'}642bo3bo9bobo13b4obo10bo2bo13bo3b3o2bo3b
o2b2o26b${'$'}569bo56b2o14bo3b2o11bo11bo2bob2o4b2o4bobo7b2o6bobo3bo4b3o2bo
26b${'$'}184bobo381bo57b2o14bo8bo3b2obo12bobo8b2o5bo8b2o7bo4b3o2bo4bo26b${'$'}
184b2o382b3o72bo3bo3b3o3bo14bo44b5o27b${'$'}185bo563b${'$'}643bo3bo3b3o3bo14bo
44b5o27b${'$'}562b2o30b2o30b2o14bo8bo3b2obo12bobo8b2o5bo8b2o7bo4b3o2bo4bo
26b${'$'}562b2o30b2o30b2o14bo3b2o11bo11bo2bob2o4b2o4bobo7b2o6bobo3bo4b3o2bo
26b${'$'}344bo297bo3bo9bobo13b4obo10bo2bo13bo3b3o2bo3bo2b2o26b${'$'}344bobo198bo
98bo3bobo26bo11b2o14bobobobo7b2o28b${'$'}344b2o198bo100bo2b2o54b2o3bob2o36b
${'$'}544b3o100b3o23bo32b4o39b${'$'}673bo8b3o23bo40b${'$'}681b5o21bo41b${'$'}681b3ob2o34b
2o26b${'$'}684b2o21b4o6b4ob2o25b${'$'}706b6o5b6o26b${'$'}706b4ob2o5b4o27b${'$'}136bobo571b
2o37b${'$'}136b2o547bo63b${'$'}137bo547bobo61b${'$'}685b2o62b3${'$'}296bo452b${'$'}296bobo198bo
251b${'$'}296b2o198bo252b${'$'}496b3o162bo55b2o30b${'$'}661bobo52b4o29b${'$'}661b2o53b2ob
2o28b${'$'}718b2o29b2${'$'}727b4o18b${'$'}726b6o17b${'$'}88bobo635b4ob2o16b${'$'}88b2o547bo64b
3o25b2o9b2o6b${'$'}89bo547bobo61b5o31b4ob2o5b${'$'}637b2o62b3ob2o16b2o12b6o6b${'$'}
665b2o37b2o16bobo13b4o7b${'$'}664bo2bo53bo2bo12bo11b${'$'}248bo412b2obo61bo9bobo
10b${'$'}248bobo198bo216b2o52bo15bobo10b${'$'}248b2o198bo212bo3b2o39b2o17bobobo
10b2o7b${'$'}448b3o212bobo22b2o15bo2bo13bo2bobo2bo9bobo6b${'$'}643b2o30b2o10bo2b
o4bo3b2o4bobo7b2o6b2o5bo2bo8bo6b${'$'}643b2o23b2o5b2o11b2o5bo3b2o5bo8b2o11b
obobo2bo4b3o6b${'$'}668b2o55b2obo3bo2bo13b2${'$'}668b2o55b2obo3bo2bo13b${'$'}3b2o30b
2o30b2o30b2o30b2o30b2o30b2o30b2o30b2o30b2o30b2o30b2o30b2o30b2o30b2o30b
2o30b2o30b2o30b2o30b2o30b2o23b2o5b2o11b2o5bo3b2o5bo8b2o11bobobo2bo4b3o
6b${'$'}3b2o30b2o3bobo24b2o30b2o30b2o30b2o30b2o30b2o30b2o30b2o30b2o30b2o30b
2o30b2o30b2o30b2o30b2o30b2o30b2o30b2o30b2o30b2o10bo2bo4bo3b2o4bobo7b2o
6b2o5bo2bo8bo6b${'$'}40b2o547bo73bobo22b2o15bo2bo13bo2bobo2bo9bobo6b${'$'}41bo
547bobo69bo3b2o39b2o17bobobo10b2o7b${'$'}589b2o75b2o52bo15bobo10b${'$'}3bo657b2o
bo61bo9bobo10b${'$'}2b3o659bo2bo53bo2bo12bo11b${'$'}bo3bo194bo464b2o37b2o16bobo
13b4o7b${'$'}ob3obo193bobo198bo299b3ob2o16b2o12b6o6b${'$'}b5o194b2o198bo300b5o
31b4ob2o5b${'$'}196b2o30b2o30b2o30b2o30b2o30b2o30b2o10b3o17b2o30b2o30b2o30b
2o30b2o152b3o25b2o9b2o6b${'$'}35b2o30b2o30b2o30b2o30b2o30bo2bo28bo2bo28bo2b
o28bo2bo28bo2bo28bo2bo28bo2bo28bo2bo28bo2bo28bo2bo28bo2bo28bo2bo175b4o
b2o16b${'$'}35bobo29bobo29bobo29bobo29bobo29bo2bo28bo2bo28bo2bo28bo2bo28bo
2bo28bo2bo28bo2bo28bo2bo28bo2bo28bo2bo28bo2bo28bo2bo175b6o17b${'$'}36b2o30b
2o30b2o30b2o30b2o30b2o30b2o30b2o30b2o30b2o30b2o30b2o30b2o30b2o30b2o30b
2o30b2o177b4o18b${'$'}6b3o692bo47b${'$'}8bo691bo48b${'$'}7bo741b${'$'}701bobo45b${'$'}703bo45b${'$'}
701bo47b${'$'}702bo46b2${'$'}38b2o30b2o30b2o30b2o30b2o30b2o30b2o30b2o30b2o30b2o
30b2o371b4o14b${'$'}4b3o30bobo29bobo29bobo29bobo29bobo29bo2bo28bo2bo28bo2bo
28bo2bo28bo2bo28bo2bo369b6o13b${'$'}3bo3bo29b2o30b2o30b2o30b2o30b2o30bo2bo
28bo2bo28bo2bo28bo2bo28bo2bo28bo2bo229b2o138b4ob2o12b${'$'}2bo5bo189b2o30b
2o30b2o30b2o30b2o30b2o229b2o115b3o25b2o9b2o2b${'$'}2b2obob2o193b2o387bo113b
5o15b3o13b4ob2ob${'$'}202bobo461b2o37b3ob2o14bo15b6o2b${'$'}202bo464b2o39b2o18bo
13b4o3b${'$'}5bo659bobo59bo12bo8b${'$'}4bobo657b3o71b2o9b${'$'}4bobo34b2o621bo2bo62bo
8bo2bo6b${'$'}5bo35bobo356b2o262bob3o39b2o19b4o7b3ob2o3b${'$'}41bo357b2o264bo2bo
21b2o15bo2bo21bo13bo2b${'$'}5b2o30b2o30b2o30b2o30b2o30b2o30b2o30b2o30b2o30b
2o30b2o30b2o30b2o10bo19b2o30b2o30b2o30b2o30b2o30b2o30b2o30b2o19b2o9b2o
10bo2bo8b2o4bobo7b2o5b4o2bob2o12bo2b${'$'}5b2o30b2o30b2o30b2o30b2o30b2o30b
2o30b2o30b2o30b2o30b2o30b2o30b2o30b2o30b2o30b2o30b2o30b2o30b2o30b2o30b
2o23b2o5b2o11b2o9b2o5bo8b2o5bobobobo4b2o5bo2b2o2b${'$'}670b2o53b2o2bobo11b
2o4b2${'$'}670b2o53b2o2bobo11b2o4b${'$'}422bo30b2o30b2o30b2o30b2o30b2o30b2o30b2o
23b2o5b2o11b2o9b2o5bo8b2o5bobobobo4b2o5bo2b2o2b${'$'}422bo30b2o30b2o30b2o
30b2o30b2o30b2o23b2o5b2o19b2o9b2o10bo2bo8b2o4bobo7b2o5b4o2bob2o12bo2b${'$'}
424b2o211b2o26bo2bo21b2o15bo2bo21bo13bo2b${'$'}250b2o387bo24bob3o39b2o19b4o
7b3ob2o3b${'$'}250bobo411bo2bo62bo8bo2bo6b${'$'}250bo413b3o71b2o9b${'$'}665bobo59bo
12bo8b${'$'}667b2o39b2o18bo13b4o3b${'$'}89b2o575b2o37b3ob2o14bo15b6o2b${'$'}89bobo
356b2o212b2o41b5o15b3o13b4ob2ob${'$'}89bo357b2o212b2o43b3o25b2o9b2o2b${'$'}449bo
213bo66b4ob2o12b${'$'}730b6o13b${'$'}731b4o14b2${'$'}479b6o6b2o229b2o25b${'$'}478bo5bo4bo
4bo225b2ob2o24b${'$'}472b2o10bo10bo190b2o32b4o25b${'$'}414b2o38b5o12b2o5bo4bo5bo
5bo189b2o34b2o26b${'$'}298b2o113b3o37bo4bo14bo6b2o8b6o191bo61b${'$'}298bobo93b5o
12b2o3bo41bo27bo262b${'$'}298bo94bo4bo12bo3bo37bo3bo28b3o260b${'$'}398bo11bo44bo
21bo7bob2o2bo257b${'$'}393bo3bo12b3o2b2o59b3o8b3o2bo256b${'$'}137b2o256bo14bobob
3o38b2o16b3o10b5o2bo255b${'$'}137bobo271b3ob2o20b2o15bo2bo14bo3bo3bo5b3obob
3o215b2o37b${'$'}137bo275b2o9b2o10bo2bo8b2o4bobo7b2o5bo2bobo4bo9b2ob2o213b
2o38b${'$'}417b2o5b2o11b2o9b2o5bo8b2o5bo8bo9b2ob2o216bo37b${'$'}417b2o53b8o252b
6o6b2o3b${'$'}731bo5bo4bo4bob${'$'}417b2o53b8o257bo10bo${'$'}360b2o30b2o23b2o5b2o11b
2o9b2o5bo8b2o5bo8bo9b2ob2o212b5o19bo4bo5bo5bo${'$'}360b2o30b2o19b2o9b2o10bo
2bo8b2o4bobo7b2o5bo2bobo4bo9b2ob2o210bo4bo20bobo8b6o${'$'}411b3ob2o20b2o15b
o2bo14bo3bo3bo5b3obob3o152b5o59bo37b${'$'}410bobob3o38b2o16b3o10b5o2bo152bo
4bo21bo32bo3bo20bo2bo14b${'$'}346b2o62b3o2b2o59b3o8b3o2bo158bo20bobo33bo21b
3ob2o13b${'$'}346bobo61bo44bo21bo7bob2o2bo154bo3bo24bo53b2o3bob2o11b${'$'}346bo
64bo3bo37bo3bo28b3o159bo21bob2o24b2obo12b2o14bo3bo9b3o2b${'$'}411b2o3bo41bo
27bo181bob2o25b4ob2o9bo2bo13bobobob2o4bo3b2ob${'$'}413b3o37bo4bo14bo6b2o8b
6o155b2o13b3o2b2o7b2ob2o11bo4b2o4b2o4bobo7b2o6b5obobo2bobo2b2o${'$'}185b2o
227b2o38b5o12b2o5bo4bo5bo5bo155b2o14b2o2b2o3bo4b3o12bobo8b2o5bo8b2o7bo
4b2o2bo5bob${'$'}185bobo284b2o10bo10bo180b2o3b2o14bo40bo3b5o2b${'$'}185bo292bo5b
o4bo4bo254b${'$'}370b2o107b6o6b2o183b2o3b2o14bo40bo3b5o2b${'$'}370bobo278b2o14b
2o2b2o3bo4b3o12bobo8b2o5bo8b2o7bo4b2o2bo5bob${'$'}370bo97bo2bo179b2o13b3o2b
2o7b2ob2o11bo4b2o4b2o4bobo7b2o6b5obobo2bobo2b2o${'$'}472bo195bob2o25b4ob2o
9bo2bo13bobobob2o4bo3b2ob${'$'}468bo3bo175bo21bob2o24b2obo12b2o14bo3bo9b3o
2b${'$'}469b4o173bo3bo24bo53b2o3bob2o11b${'$'}651bo20bobo33bo21b3ob2o13b${'$'}646bo4b
o21bo32bo3bo20bo2bo14b${'$'}394b2o251b5o59bo37b${'$'}394bobo309bo4bo20bobo8b6o${'$'}
394bo312b5o19bo4bo5bo5bo${'$'}737bo10bo${'$'}731bo5bo4bo4bob${'$'}233b2o497b6o6b2o3b${'$'}
233bobo513b${'$'}233bo487bo2bo24b${'$'}418b2o305bo23b${'$'}418bobo300bo3bo23b${'$'}418bo
303b4o23b6${'$'}442b2o305b${'$'}442bobo304b${'$'}442bo306b3${'$'}281b2o466b${'$'}281bobo465b${'$'}
281bo467b${'$'}466b2o281b${'$'}466bobo280b${'$'}466bo282b${'$'}491b2o256b${'$'}487b4ob2o5b4o
246b${'$'}487b6o5b6o245b${'$'}465b2o21b4o6b4ob2o244b${'$'}462b3ob2o34b2o245b${'$'}462b5o
21bo260b${'$'}454bo8b3o23bo259b${'$'}428b3o23bo32b4o258b${'$'}426bo2b2o54b2o3bob2o
255b${'$'}425bo3bobo26bo11b2o14bobobobo7b2o247b${'$'}329b2o92bo3bo9bobo13b4obo
10bo2bo13bo3b3o2bo3bo2b2o245b${'$'}329bobo11b2o30b2o30b2o14bo3b2o11bo11bo2b
ob2o4b2o4bobo7b2o6bobo3bo4b3o2bo245b${'$'}329bo13b2o30b2o30b2o14bo8bo3b2obo
12bobo8b2o5bo8b2o7bo4b3o2bo4bo245b${'$'}424bo3bo3b3o3bo14bo44b5o246b2${'$'}424bo
3bo3b3o3bo14bo44b5o246b${'$'}407b2o14bo8bo3b2obo12bobo8b2o5bo8b2o7bo4b3o2bo
4bo245b${'$'}407b2o14bo3b2o11bo11bo2bob2o4b2o4bobo7b2o6bobo3bo4b3o2bo245b${'$'}
353b2o68bo3bo9bobo13b4obo10bo2bo13bo3b3o2bo3bo2b2o245b${'$'}353bobo69bo3bob
o26bo11b2o14bobobobo7b2o247b${'$'}353bo49b3o20bo2b2o54b2o3bob2o255b${'$'}402b5o
21b3o23bo32b4o258b${'$'}402b3ob2o46bo8b3o23bo259b${'$'}405b2o55b5o21bo260b${'$'}462b
3ob2o34b2o245b${'$'}465b2o21b4o6b4ob2o244b${'$'}377b2o108b6o5b6o245b${'$'}377bobo107b
4ob2o5b4o246b${'$'}377bo113b2o256b2${'$'}478b2o269b${'$'}477b4o268b${'$'}477b2ob2o267b${'$'}
479b2o268b${'$'}401b2o346b${'$'}401bobo16b2o327b${'$'}401bo14b4ob2o5b4o317b${'$'}416b6o5b
6o316b${'$'}374bo19b2o21b4o6b4ob2o315b${'$'}372b2obo15b3ob2o34b2o316b${'$'}334b2o35bo
3bo15b5o353b${'$'}331b3ob2o30b4obobo17b3o354b${'$'}331b5o30bobo3b2o12b2o33bo327b
${'$'}332b3o20bo4b2o4bobo16bo2bo32b2o5b3o318b${'$'}354bobo2b2obo2b2ob2o18bo15bo
14bo2bo3b2o321b${'$'}353bo3bo2b2ob3o2b4o11b2o5b2o11b2o12b3o6bo4b2o316b${'$'}340b
2o12bo2bo4bo3b2obob2o17b2o4b2o4b3o7b2o4b2o5b2ob2o2bo316b${'$'}340b2o12b2ob
2o9b2ob2o11bobo9b2o14b2o5bobo10bo316b${'$'}370bo14bo35bo7b3o317b2${'$'}370bo14bo
35bo7b3o317b${'$'}340b2o12b2ob2o9b2ob2o11bobo9b2o14b2o5bobo10bo316b${'$'}340b2o
12bo2bo4bo3b2obob2o17b2o4b2o4b3o7b2o4b2o5b2ob2o2bo316b${'$'}353bo3bo2b2ob3o
2b4o11b2o5b2o11b2o12b3o6bo4b2o316b${'$'}354bobo2b2obo2b2ob2o18bo15bo14bo2bo
3b2o321b${'$'}332b3o20bo4b2o4bobo16bo2bo32b2o5b3o318b${'$'}331b5o30bobo3b2o12b2o
33bo327b${'$'}331b3ob2o30b4obobo17b3o354b${'$'}334b2o35bo3bo15b5o353b${'$'}372b2obo
15b3ob2o34b2o316b${'$'}374bo19b2o21b4o6b4ob2o315b${'$'}416b6o5b6o316b${'$'}416b4ob2o
5b4o317b${'$'}403b2o15b2o327b${'$'}402bo346b${'$'}407b2o340b${'$'}406b4o339b${'$'}406b2ob2o338b
${'$'}408b2o!"""
    }
    actual val TEN_ENGINE_CORDERSHIP: String by lazy { TODO() }
    actual val THREE_ENGINE_CORDERSHIP_GUN: String by lazy { TODO() }
    actual val THREE_ENGINE_CORDERSHIP_RAKE: String by lazy { TODO() }
    actual val INFINITE_GLIDER_HOTEL_FOUR: String by lazy { """#N Infinite glider hotel 4
#O Ivan Fomichev
#C Another compact infinite glider hotel, created on January 28, 2015.
#C Based on prior art by David Bell.
#C www.conwaylife.com/wiki/index.php?title=Infinite_glider_hotel
x = 198, y = 165, rule = B3/S23
109b3o${'$'}109bobo${'$'}44bo63bo3bo${'$'}32bo11b3o61bo3bo${'$'}30b3o14bo23b2o20b2o17bo${'$'}
14bo14bo16b2o23bo21b2o11b2o4bo${'$'}14b3o12b2o38bobo33bo6bo${'$'}17bo47b2o2b2o
34bo5bo${'$'}16b2o20b2o25b2o43b2o${'$'}33b3obobo3bobo2b2o55bo3bo${'$'}25bo11bobo2bo3b
ob3o43b2o10bobo${'$'}17b2o5b3o10b3o3bo6b2o42bo${'$'}17b2o4b2ob2o8bo3bo3b5obo34b
2o6b3o${'$'}23bo12bo4bo4b4o35b2o22b3o${'$'}36bo3bo6bo46bo13bo3bo${'$'}25bo13bo29b2o
22bo14bo2bo${'$'}25b2o36b2obobobo22b2o13b2o${'$'}24bob2o38bo${'$'}62bo50bo${'$'}33b2o27bo
8b2o39bobo2b2o7b2o${'$'}33bo19b2o16bobo3b2o32bo3b4o6b3o${'$'}35bo16bobo8bo4bo4bo
3b2o35bo7bobob2o${'$'}34b2o16bo12bo2bo4b2o21b2obo9b2obo10b2o${'$'}30b2o19b2o4b2o
6b3o29bo3bo8bobo${'$'}30bo24bo2bo38bo3bo9bo27bo${'$'}31b3o21b2o29bo12bobo37bo${'$'}
18bobo12bo30b2o20bo51bobo${'$'}18b2o44b2o19bobo47bo2b2o${'$'}19bo62bo2b2o22bo24b
o5bo${'$'}81bo5bo20bobo23bo5bo${'$'}14b2o65bo5bo20bobo11b3o10bo4bo${'$'}13bobo66bo4bo
21bo26bo${'$'}13bo69bo53b3o${'$'}12b2o70b3o34bo${'$'}21bo52b2o44bobo${'$'}20b3o51bo46bo15b
2ob2o${'$'}19b2o2bo48bobo9b2ob2o47bo${'$'}22b3o47b2o9bo56b2o${'$'}87b2o48b3o${'$'}84b3o2bo
bo${'$'}89bobo4b3obo${'$'}19bob2ob3o27bo33bo5bo4bob2o${'$'}19b3obo2bo4b2o21b3o8bo22bo
2bo2bo4b2o${'$'}19b2ob2ob2o2bo2bo24bo5b2ob2o20bo2bo2bo${'$'}23b3o3b2o4b2o19b2o5b
3ob2o21bo4bo${'$'}13b2o8b2o10bo16b2o10b5o27bo2bo${'$'}14bo18bobo16bo12b3o29b2o${'$'}
14bobo16b2o19bo11bo67b2o${'$'}15b2o36b2o79b2o${'$'}114bo${'$'}29b2o46b2o35bo${'$'}28bo2bo
45bo35bobo${'$'}13b2o13bo2bo43bobo32bo2b2o${'$'}13b2o13b5o42b2o32bo5bo${'$'}31b2ob2o
16b2o15b2o38bo5bo${'$'}33bo17bo2bo55bo4bo10b2o18b3o${'$'}13b3o35bo2bo17b2o37bo
14b2o17bo${'$'}14b2o36bobo20bo36b3o30bo3bo${'$'}11b2o59bobo70bo2bobo8bo${'$'}11b3o59b
o73bobo2bo4b3o${'$'}12bobo97b2ob2o31bo3bo3bo${'$'}13b2o42b2o52bo40bo3b2o${'$'}40b2o
16bo7b2o47b2o32b3o${'$'}40bo14b3o4b2o2b2o2b2o40b3o3b2o34bo${'$'}4b2o5b2o28b3o11b
o6b2o2bo2bobo46b2o33bobo${'$'}5bo5b2o11b2o17bo24b3o4b3o76b2o2b2o${'$'}5bobo7bo8b
o43b2o7bo11b2o5b2o60b2o${'$'}6b2o7bobo7b3o48bo12bo7bo${'$'}10b2o3b2o10bo62b3ob3o
${'$'}9bobo80bobo61b2o${'$'}6b2o2bo81bobo61bobo${'$'}93bo62bo${'$'}5bo3bo79bo${'$'}4bo4bo80bo${'$'}
3bobobo80b3o${'$'}2bobobo${'$'}o4bo${'$'}o3bo${'$'}84b2o10bo${'$'}2b2o79bobo9b2o${'$'}83bo11bobo${'$'}59b
3o20b2o56bobo${'$'}27bobo31bo78b2o${'$'}28b2o30bo80bo${'$'}28bo${'$'}172b2o${'$'}172bobo${'$'}172bo${'$'}
195bo${'$'}194b3o${'$'}193bob3o${'$'}79b2o111bo3bo${'$'}79b2o3b3o104bo3bo${'$'}82b2o106b3obo${'$'}
87bo103b3o${'$'}82b2ob2o105bo${'$'}188bo${'$'}43b3o78bobo60bobo${'$'}42b2o2bo37b3o37b2o45b
o15b2o${'$'}40b2o29b2o14bo37bo45b3o17b2o${'$'}39bo7bo23b2o10bo4bo85bo16bobo${'$'}39b
2o42bo5bo41b2o22bo17b2o4b2o5b2o5bo${'$'}42b2obobo35bo5bo37b2obo2bob2o6bo11b
3o22b2o4b2o5b2o${'$'}44bo39b2o2bo29b2o7b2o2bo4bo4b3o14bo20bo${'$'}48bo34bobo32b
2o12bo7bo16b2o${'$'}41b2o41bo48bobo4b2o42b2o${'$'}42bo3bob3o33bo99b2o${'$'}39b3o6b2ob
o11b2o${'$'}39bo8bob2o11b2o67bo10bobo19bo19bo${'$'}49b3obo46b2o24b3o2bob2o8bo20b
6o14bobo${'$'}99bo2bo21b2ob2obo2bob2o26bo2b2ob2o12bo2bo${'$'}51bo51bo4bo18bob2o
4b2o5b2o3bo14b2obo3b2o11bo2bo${'$'}104bo2bo2bo15bo2bo3b2o7b2obobo15bob4o${'$'}
98b2o4bo2bo2bo11bo7bo2bo10bo21b2o14bo2bo${'$'}96b2obo4bo5bo10bob2o59b2o${'$'}98b
ob3o4bobo11bo${'$'}107bobo2b3o5b2o${'$'}59b3o48b2o${'$'}57b2o56bo28b2o36b2o${'$'}62bo47b2o
b2o29bo19b2o16bobo${'$'}57b2ob2o15bo68bo16bobo18bo${'$'}76bobo66b2o16bo13bo6b2o${'$'}
77bo34b3o26b2o19b2o4b2o4bo4bo${'$'}59b3o53bo15b3o7bo24bo2bo4bo5bo${'$'}62bo26bo
21bo4bo14bobo8b3o21b2o6bo${'$'}58bo4bo10b3o11bobo20bo5bo13bo2bo9bo31bo2bo${'$'}
58bo5bo23bobo20bo5bo14b3o41b3o${'$'}58bo5bo24bo22b2o2bo15b3o${'$'}59b2o2bo47bobo
16bo2bo${'$'}58bobo51bo12b2o6bo${'$'}59bo37bobo12bo11bobo3bo${'$'}59bo27bo9bo3bo22bo${'$'}
86bobo8bo3bo21b2o${'$'}74b2o10bob2o9bob2o82b2o${'$'}71b2obobo7bo35b2o63bo${'$'}71b3o
6b4o3bo32b2o12b2o47bobo${'$'}71b2o7b2o2bobo96b2o${'$'}85bo2${'$'}89b2o13b2o26bobo${'$'}87b
o2bo14bo27bo31bo${'$'}86bo3bo13bo29bo7b2o21b3o8b2o${'$'}87b3o22b2o16bo9bo2bo24bo
6bobo${'$'}103b3o6b2o16bo9b2o4b2o19b2o8bo${'$'}104bo19b2o20bo16b2o${'$'}90bobo10b2o
20bo18bobo16bo8b2o${'$'}89bo3bo31bobo16b2o19bo5b2o2bo${'$'}87b2o37b2o36b2o4bo4bo
${'$'}87bo5bo38bo3b2o32b4obo${'$'}86bo6bo37b3ob4o32bo2b2o${'$'}86bo4b2o11b2o24b2o6bo
33b3o${'$'}86bo17b2o23b3o5bo19bo2bo3b2o7bo${'$'}86bo3bo40bo4bo20bo${'$'}86bo3bo41bo
26b2obo${'$'}87bobo42bo2bo17b3o3bob2obo15b2o${'$'}87b3o42b3o18bo2b3o2b2obo15b2o${'$'}
153bo5bo2bo${'$'}149bo4b2o2bo3b3o${'$'}132b2o16bo5b2o23b2o${'$'}128b2o2b2o14b3o30bo${'$'}
127bobo38b2o12b3o${'$'}127bo23b2o16bo14bo${'$'}126b2o23bo14b3o${'$'}152b3o11bo${'$'}154bo!""" }
    actual val SPAGHETTI_MONSTER: String by lazy { """#N Spaghetti monster
#O Tim Coe
#C The first 3c/7 orthogonal spaceship to be discovered.
#C http://conwaylife.com/wiki/Spaghetti_monster
x = 27, y = 137, rule = B3/S23
8b3o5b3o${'$'}8bobo5bobo${'$'}8bobo5bobo${'$'}6bob2o3bo3b2obo${'$'}6b2o4bobo4b2o${'$'}10b2obob
2o${'$'}9bo7bo${'$'}9bobo3bobo${'$'}5b5o7b5o${'$'}4bo2bo11bo2bo${'$'}5bob3o7b3obo${'$'}7bob2o5b2obo${'$'}
6b2obobo3bobob2o${'$'}6b3obo5bob3o2${'$'}10b2o3b2o${'$'}12bobo${'$'}9bo7bo${'$'}9b2o5b2o${'$'}6b2o
11b2o${'$'}4bob2o11b2obo${'$'}4b2o2b2o7b2o2b2o${'$'}4bo2bo2bo5bo2bo2bo${'$'}5bo4bo5bo4bo${'$'}
5bo2bo2bo3bo2bo2bo${'$'}2bo5bo9bo5bo${'$'}3bobo15bobo${'$'}7bo11bo${'$'}3bo3bobo7bobo3bo${'$'}
3bo2bo3bo5bo3bo2bo${'$'}4b2o2b2o7b2o2b2o${'$'}8bo9bo2${'$'}8b5ob5o${'$'}bo6b2ob2ob2ob2o6bo
${'$'}3o7bo5bo7b3o${'$'}o2b2o5bo5bo5b2o2bo${'$'}2bo3b5o5b5o3bo${'$'}7bob2o5b2obo${'$'}bo3bo15bo
3bo${'$'}bob2o2bo11bo2b2obo${'$'}bob4o13b4obo${'$'}4bo17bo2${'$'}2bo21bo${'$'}bobo19bobo${'$'}o25bo${'$'}
o3bo17bo3bo${'$'}5bo15bo${'$'}2o23b2o${'$'}2bo3bo2bo7bo2bo3bo${'$'}2bo3bobobo5bobobo3bo${'$'}2b
o5bob2o3b2obo5bo${'$'}2bo3b2obo7bob2o3bo${'$'}6b2o11b2o${'$'}4bo17bo${'$'}3bo19bo${'$'}3bo4bo9b
o4bo${'$'}2b2o3b2o9b2o3b2o${'$'}2b2o3bobo7bobo3b2o${'$'}2b2o3b2o3b3o3b2o3b2o${'$'}2b3o2b3o
bo3bob3o2b3o${'$'}6bob2obo3bob2obo${'$'}2b2o3b2obo5bob2o3b2o${'$'}3bob2o3bobobobo3b2o
bo${'$'}11bobobo${'$'}8bo9bo${'$'}8b3o5b3o${'$'}10b2obob2o${'$'}10b7o${'$'}8b3o5b3o${'$'}7b2obobobobob2o${'$'}
6bo3bo5bo3bo${'$'}11b2ob2o${'$'}5bo2bobobobobobo2bo${'$'}6b4o7b4o${'$'}9bo7bo${'$'}9bo7bo${'$'}6b2ob
o2bobo2bob2o2${'$'}9b2o5b2o3${'$'}9bo7bo${'$'}9b3o3b3o${'$'}8bo2bo3bo2bo${'$'}9bo7bo${'$'}8bo2bo3bo
2bo${'$'}11b2ob2o${'$'}12bobo${'$'}10bobobobo${'$'}9bo3bo3bo${'$'}9bo7bo${'$'}12bobo${'$'}7b2obo5bob2o${'$'}7b
2o2bo3bo2b2o${'$'}7bo11bo${'$'}8bo9bo${'$'}6bobo9bobo${'$'}5b4o9b4o${'$'}5b2obobo5bobob2o${'$'}4bo2b
o11bo2bo${'$'}9bobo3bobo${'$'}8b2obo3bob2o${'$'}4bo2bo3b2ob2o3bo2bo${'$'}9bo2bobo2bo${'$'}6bo2b
ob2ob2obo2bo${'$'}7bobobobobobobo${'$'}8b2o2bobo2b2o${'$'}9bobo3bobo${'$'}10b2o3b2o${'$'}7b2o9b
2o${'$'}7b3o7b3o${'$'}7bobo7bobo${'$'}5b2o2bo7bo2b2o${'$'}5b2o13b2o${'$'}11bo3bo${'$'}6bo4bo3bo4bo${'$'}
6b2o3bo3bo3b2o${'$'}7bo2bo5bo2bo${'$'}7b3o7b3o${'$'}6bobo9bobo${'$'}6b2o11b2o${'$'}6bobo4bo4bob
o${'$'}6b2o4b3o4b2o${'$'}6b2o3bo3bo3b2o${'$'}5b3o4b3o4b3o${'$'}3b2o17b2o${'$'}2bo5b2o2bobo2b2o
5bo2${'$'}2bo2bob3ob2ob2ob3obo2bo${'$'}8b3o5b3o${'$'}10b3ob3o${'$'}5bo4b2obob2o4bo${'$'}11bo3bo
2${'$'}11b2ob2o!""" }
    actual val FERMAT_PRIME_CALCULATOR: String by lazy { TODO() }
    actual val MAX: String by lazy {
        """
        #N Max
        #O Tim Coe
        #C A spacefiller that fills space with zebra stripes.
        #C www.conwaylife.com/wiki/index.php?title=Max
        x = 27, y = 27, rule = s23/b3
        18bo8b${'$'}17b3o7b${'$'}12b3o4b2o6b${'$'}11bo2b3o2bob2o4b${'$'}10bo3bobo2bobo5b${'$'}10bo4bobo
        bobob2o2b${'$'}12bo4bobo3b2o2b${'$'}4o5bobo4bo3bob3o2b${'$'}o3b2obob3ob2o9b2ob${'$'}o5b2o
        5bo13b${'$'}bo2b2obo2bo2bob2o10b${'$'}7bobobobobobo5b4o${'$'}bo2b2obo2bo2bo2b2obob2o
        3bo${'$'}o5b2o3bobobo3b2o5bo${'$'}o3b2obob2o2bo2bo2bob2o2bob${'$'}4o5bobobobobobo7b${'$'}
        10b2obo2bo2bob2o2bob${'$'}13bo5b2o5bo${'$'}b2o9b2ob3obob2o3bo${'$'}2b3obo3bo4bobo5b4o
        ${'$'}2b2o3bobo4bo12b${'$'}2b2obobobobo4bo10b${'$'}5bobo2bobo3bo10b${'$'}4b2obo2b3o2bo11b${'$'}
        6b2o4b3o12b${'$'}7b3o17b${'$'}8bo!
    """.trimIndent()
    }
    actual val TEST_PATTERN: String by lazy {
        """
        #N Rumbling river 1
        #O Dean Hickerson
        #C A period 3 oscillator. A rumbling river that was found in November 1994.
        #C www.conwaylife.com/wiki/index.php?title=Rumbling_river_1
        x = 49, y = 12, rule = B3/S23
        14b2o6b2o6b2o3bob2o10b${'$'}4bo8bo2bo4bo2bo4bo2bo2b2obo10b${'$'}o2bobo4bo3b2o2bo
        3b2o2bo3bobo5bob2o7b${'$'}4obo2b6o2b6o2b6o2b6obobo7b${'$'}5bobo5bobo5bobo5bobo5b
        obo6b2ob${'$'}2b2obobobobo3bobobo3bobobo3bobobo3bobo5bob${'$'}bo5bobo3bobobo3bob
        obo3bobobo3bobobobob2o2b${'$'}b2o6bobo5bobo5bobo5bobo5bobo5b${'$'}7bobob6o2b6o2b
        6o2b6o2bob4o${'$'}7b2obo5bobo3bo2b2o3bo2b2o3bo4bobo2bo${'$'}10bob2o2bo2bo4bo2bo
        4bo2bo8bo4b${'$'}10b2obo3b2o6b2o6b2o!
    """.trimIndent()
    }
    actual val NOAHS_ARK: String by lazy {
        """#N Noah's ark
#O Charles Corderman
#C A diagonal period 1344 c/12 puffer made up of two switch engines that was found in 1971.
#C www.conwaylife.com/wiki/index.php?title=Noah's_ark
x = 15, y = 15, rule = 23/3
10bobo2b${'$'}9bo5b${'$'}10bo2bob${'$'}12b3o6${'$'}bo13b${'$'}obo12b2${'$'}o2bo11b${'$'}2b2o11b${'$'}3bo!"""
    }
    actual val FROTHING_PUFFER: String by lazy {
        """#N Frothing puffer
#O Paul Tooke, April 2001
#C A c/2 puffer with a seemingly unstable back end that nevertheless
#C survives.
#C http://www.conwaylife.com/wiki/Frothing_puffer
x = 33, y = 23, rule = B3/S23
7bo17bo${'$'}6b3o15b3o${'$'}5b2o4b3o5b3o4b2o${'$'}3b2obo2b3o2bo3bo2b3o2bob2o${'$'}4bobo2bo
bo3bobo3bobo2bobo${'$'}b2obobobobo4bobo4bobobobob2o${'$'}b2o3bobo4bo5bo4bobo3b2o
${'$'}b3obo3bo4bobobo4bo3bob3o${'$'}2o9b2obobobob2o9b2o${'$'}12bo7bo${'$'}9b2obo7bob2o${'$'}10b
o11bo${'$'}7b2obo11bob2o${'$'}7b2o15b2o${'$'}7bobobob3ob3obobobo${'$'}6b2o3bo3bobo3bo3b2o${'$'}
6bo2bo3bobobobo3bo2bo${'$'}9b2o4bobo4b2o${'$'}5b2o4bo3bobo3bo4b2o${'$'}9bob2obo3bob2o
bo${'$'}10bobobobobobobo${'$'}12bo2bobo2bo${'$'}11bobo5bobo!"""
    }
}