#!/usr/bin/env python3
"""Build four original offline bilingual story MP4s, each exactly five minutes."""
from pathlib import Path
import math, subprocess, sys, wave
import numpy as np
from PIL import Image, ImageDraw, ImageFont

OUT=Path(sys.argv[1] if len(sys.argv)>1 else "media"); OUT.mkdir(parents=True,exist_ok=True)
W,H,FPS,DURATION,SR=640,360,12,300,22050
FONT="/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf"
BOLD="/usr/share/fonts/truetype/dejavu/DejaVuSans-Bold.ttf"
def face(n,b=False):
    try:return ImageFont.truetype(BOLD if b else FONT,n)
    except OSError:return ImageFont.load_default()
def lines(d,s,f,w):
    out=[]; cur=""
    for word in s.split():
        t=(cur+" "+word).strip()
        if d.textbbox((0,0),t,font=f)[2]<=w:cur=t
        else:
            if cur:out.append(cur)
            cur=word
    if cur:out.append(cur)
    return out
def person(d,x,y,color,name):
    d.ellipse((x-13,y-35,x+13,y-9),fill="#ffd8b4",outline="#66536e",width=2)
    d.arc((x-14,y-38,x+14,y-8),180,355,fill="#504052",width=6)
    d.rounded_rectangle((x-15,y-9,x+15,y+31),radius=8,fill=color,outline="#51466b",width=2)
    d.line((x-9,y+30,x-16,y+54),fill="#51466b",width=4);d.line((x+9,y+30,x+16,y+54),fill="#51466b",width=4)
    d.text((x,y+62),name,font=face(11,True),fill="white",anchor="mm",stroke_width=1,stroke_fill="#51466b")
def art(kind,i,title,en,idn,palette):
    top,bot=palette;a=tuple(int(top[k:k+2],16) for k in (1,3,5));b=tuple(int(bot[k:k+2],16) for k in (1,3,5))
    im=Image.new("RGB",(W,H));p=im.load()
    for y in range(H):
        t=y/(H-1);c=tuple(int(a[k]*(1-t)+b[k]*t) for k in range(3))
        for x in range(W):p[x,y]=c
    d=ImageDraw.Draw(im,"RGBA")
    for n in range(18):
        x=(n*83+i*37)%W;y=(n*47+i*19)%220;d.ellipse((x,y,x+3,y+3),fill=(255,255,255,125))
    d.ellipse((W-120,28,W-55,93),fill=(255,246,203,210))
    if kind=="bridge":
        d.rectangle((0,224,W,300),fill=(63,156,183,255))
        d.polygon([(0,224),(0,178),(80,143),(160,180),(235,145),(310,190),(390,150),(470,180),(550,137),(640,175),(640,255)],fill=(87,153,129,255))
        for x,c in [(45,"#f2bd86"),(115,"#f3e2b9"),(500,"#d9d5ff"),(565,"#f3c6d4")]:
            d.rounded_rectangle((x,135,x+64,230),radius=8,fill=c,outline="#5d5578",width=2);d.polygon([(x-8,140),(x+32,108),(x+72,140)],fill="#8067a5")
        d.arc((210,183,435,315),180,360,fill="#9d7159",width=13);d.line((225,247,416,247),fill="#a77f61",width=9)
        person(d,280,183,"#f29b87","Nara");person(d,365,183,"#82aee4","Leo")
    elif kind=="music":
        d.rounded_rectangle((80,130,560,275),radius=20,fill=(255,255,255,75),outline=(255,255,255,170),width=3)
        d.rounded_rectangle((120,205,300,255),radius=8,fill="#f7f5ff",outline="#514c6c",width=3)
        for x in range(130,300,22):d.line((x,206,x,254),fill="#6b6683",width=2)
        for x in (145,189,233,277):d.rounded_rectangle((x,205,x+12,233),radius=2,fill="#4f4b63")
        d.ellipse((385,198,440,253),fill="#d99a53",outline="#684b48",width=3);d.ellipse((401,218,424,240),fill="#644d4b")
        d.line((414,223,462,135),fill="#b47b46",width=10)
        person(d,345,178,"#a2d5ac","Mia");person(d,500,180,"#f1ad9c","Ari")
        for x,y in ((330,132),(485,120),(365,110)):d.text((x,y),"♪",font=face(25,True),fill="#fff5c7")
    elif kind=="library":
        for x,y in ((90,160),(185,130),(470,150),(545,190)):
            d.ellipse((x-42,y-20,x+42,y+28),fill=(255,255,255,180));d.ellipse((x-22,y-42,x+24,y+22),fill=(255,255,255,190))
        d.rounded_rectangle((235,143,405,262),radius=13,fill=(255,255,255,220),outline="#7165a5",width=3)
        d.polygon([(240,160),(320,180),(320,250),(240,230)],fill="#f5d89a");d.polygon([(320,180),(400,160),(400,230),(320,250)],fill="#d9e9fa")
        d.line((320,180,320,250),fill="#7e7196",width=3);d.text((320,198),"ABC",font=face(24,True),fill="#7a69aa",anchor="mm")
        person(d,170,200,"#87b9d8","Zee");person(d,470,200,"#f2b08d","Nova")
    else:
        d.ellipse((80,130,560,350),fill=(93,165,130,120));d.rounded_rectangle((205,142,435,267),radius=14,fill="#fff4cd",outline="#7e6f8e",width=3)
        d.line((240,230,290,182,350,222,399,170),fill="#70a88d",width=7)
        for x,y,c in ((240,230,"#d77d8c"),(290,182,"#6ca6cb"),(350,222,"#e3a75e"),(399,170,"#7c9a74")):d.ellipse((x-9,y-9,x+9,y+9),fill=c)
        person(d,125,213,"#e8a1ad","Lio");person(d,520,213,"#86bad6","Mara")
        d.text((315,294),"♥ SHARE · LEARN · HELP ♥",font=face(16,True),fill="white",anchor="mm")
    d.rounded_rectangle((25,20,430,78),radius=16,fill=(30,36,72,190),outline=(255,255,255,90))
    d.text((44,31),title,font=face(20,True),fill="white");d.text((45,58),f"ARDYIL ORIGINAL · SCENE {i+1}/5",font=face(10,True),fill="#dbe8ff")
    d.rounded_rectangle((22,286,618,344),radius=14,fill=(30,36,72,220),outline=(255,255,255,80))
    enline=(lines(d,en,face(13,True),560) or [en])[0];idline=(lines(d,idn,face(11),560) or [idn])[0]
    d.text((40,296),enline,font=face(13,True),fill="white");d.text((40,321),idline,font=face(11),fill="#e0f7ef")
    return im
def soundtrack(path,seed):
    rng=np.random.default_rng(seed);n=SR*DURATION;audio=np.zeros(n,dtype=np.float32)
    beat=60/(78+seed%5);bar=4*beat
    chords=[[130.81,164.81,196],[174.61,220,261.63],[196,246.94,293.66],[110,130.81,164.81]]
    def note(start,freq,dur,amp,guitar=False):
        j=int(start*SR);m=min(int(dur*SR),n-j)
        if m<=0:return
        t=np.arange(m,dtype=np.float32)/SR
        sig=np.sin(2*np.pi*freq*t)+(.16 if guitar else .25)*np.sin(2*np.pi*freq*2*t)
        audio[j:j+m]+=amp*sig*(1-np.exp(-t*32))*np.exp(-t*(2.5 if guitar else 1.7))
    prog=[0,1,2,3,0,3,1,2]
    for bi in range(math.ceil(DURATION/bar)):
        chord=chords[prog[(bi+seed)%8]];t0=bi*bar
        for j,f in enumerate(chord):note(t0+j*.035,f,2.4,.1)
        for k,j in enumerate([0,1,2,1,0,1,2,1]):note(t0+k*beat/2,chord[j]*2,.85,.045,True)
        if bi%2==0:note(t0+2*beat,rng.choice(chord)*2,1.15,.035)
    audio=np.clip(audio/((float(np.max(np.abs(audio))) or 1))*.72,-1,1)
    pcm=(np.column_stack((audio,audio*.985))*32767).astype("<i2")
    with wave.open(str(path),"wb") as w:w.setnchannels(2);w.setsampwidth(2);w.setframerate(SR);w.writeframes(pcm.tobytes())
STORIES=[
("the-paper-bridge","The Paper Bridge","bridge",[("#dceeff","#8bb7bf"),("#f4e7d4","#a6d6c0"),("#d9e8fa","#92b6d1"),("#f7e7d8","#a4cdb8"),("#dceeff","#98c8bd")],[("A Folded Map","Nara finds a paper map showing a bridge between two neighborhoods.","Nara menemukan peta yang menunjukkan jembatan di antara dua lingkungan."),("A Community Plan","Friends share ideas and design a safe crossing together.","Teman-teman berbagi ide dan merancang jalur penghubung yang aman."),("Small Helpful Steps","Everyone brings a useful skill and works carefully as a team.","Semua orang membawa keterampilan dan bekerja dengan cermat sebagai tim."),("The First Crossing","The bridge opens, and neighbors meet each other with smiles.","Jembatan dibuka, dan para tetangga saling bertemu dengan senyum."),("Connected Places","Nara learns that good communication can bring people closer.","Nara belajar bahwa komunikasi yang baik dapat mendekatkan orang-orang.")]),
("little-music-makers","Little Music Makers","music",[("#f3e1f2","#9caed8"),("#e4efff","#b8d9d2"),("#f6e8d5","#d9add0"),("#e4efff","#b8c4e8"),("#f4e5e9","#a6d7d0")],[("A Quiet Music Room","Friends discover an unused music room at their school.","Teman-teman menemukan ruang musik yang lama tidak digunakan di sekolah."),("Choosing a Sound","They explore gentle piano notes and soft guitar patterns.","Mereka mencoba nada piano lembut dan pola gitar yang tenang."),("Practice Together","Each student practices a short part and listens to the others.","Setiap siswa berlatih bagian singkat dan mendengarkan yang lain."),("A Friendly Concert","The friends share an original instrumental performance.","Teman-teman membagikan pertunjukan instrumental orisinal."),("A New Tradition","They decide to keep learning and creating music together.","Mereka memutuskan terus belajar dan menciptakan musik bersama.")]),
("the-cloud-library","The Cloud Library","library",[("#dbeaff","#8aabc9"),("#e9e8fb","#b2c9e7"),("#d9e8fa","#9fc8db"),("#eee7ff","#b9c7e8"),("#e2f0ff","#a4c9d0")],[("A Floating Book","A bright book appears beside the window of Orbit Academy.","Sebuah buku bercahaya muncul di dekat jendela Orbit Academy."),("Words Among Stars","Its pages contain new words from places across the galaxy.","Halaman-halamannya berisi kosakata baru dari berbagai tempat di galaksi."),("The Reading Circle","Friends take turns reading and explaining each new phrase.","Teman-teman bergantian membaca dan menjelaskan setiap ungkapan baru."),("A Shared Discovery","They find a story that connects all of their ideas.","Mereka menemukan cerita yang menghubungkan semua ide mereka."),("Keep Exploring","The library reminds them that curiosity makes learning grow.","Perpustakaan itu mengingatkan mereka bahwa rasa ingin tahu membuat pembelajaran berkembang.")]),
("a-map-of-kindness","A Map of Kindness","kindness",[("#f8e9d4","#9dc9a8"),("#e2f1de","#a6ceb6"),("#fae8df","#9bc4ae"),("#e8effd","#a4c9b0"),("#fbebd9","#8fbda6")],[("The First Note","Lio finds a note inviting neighbors to share helpful ideas.","Lio menemukan catatan yang mengajak tetangga berbagi ide bermanfaat."),("A Helpful Route","The group marks places where people can learn and help one another.","Kelompok itu menandai tempat orang dapat belajar dan saling membantu."),("Many Small Actions","Every kind action becomes a new point on their map.","Setiap tindakan baik menjadi titik baru pada peta mereka."),("A Growing Community","More friends join and add their thoughtful suggestions.","Lebih banyak teman bergabung dan menambahkan saran mereka."),("The Map Continues","Lio learns that kindness is a journey built one step at a time.","Lio belajar bahwa kebaikan adalah perjalanan yang dibangun selangkah demi selangkah.")])]
for idx,(slug,title,kind,palettes,scenes) in enumerate(STORIES):
    art(kind,0,title,scenes[0][1],scenes[0][2],palettes[0]).save(OUT/f"{slug}-poster.jpg",quality=82,optimize=True)
    pics=[]
    for i,(_,en,idn) in enumerate(scenes):
        p=OUT/f"{slug}-scene-{i+1}.jpg";art(kind,i,title,en,idn,palettes[i]).save(p,quality=88,optimize=True);pics.append(p)
    wav=OUT/f"{slug}-music.wav";soundtrack(wav,idx+7)
    cmd=["ffmpeg","-hide_banner","-loglevel","error","-y"]
    for p in pics:cmd+=["-loop","1","-framerate",str(FPS),"-t","60.8","-i",str(p)]
    cmd+=["-i",str(wav)]
    filters=[]
    for i in range(5):filters.append(f"[{i}:v]scale={W}:{H},zoompan=z='min(zoom+0.00005,1.05)':x='iw/2-(iw/zoom/2)':y='ih/2-(ih/zoom/2)':d=1:s={W}x{H}:fps={FPS},setsar=1,format=yuv420p[v{i}]")
    filters += ["[v0][v1]xfade=transition=fade:duration=1:offset=59.8[x1]","[x1][v2]xfade=transition=fade:duration=1:offset=119.6[x2]","[x2][v3]xfade=transition=fade:duration=1:offset=179.4[x3]","[x3][v4]xfade=transition=fade:duration=1:offset=239.2[vout]"]
    cmd+=["-filter_complex",";".join(filters),"-map","[vout]","-map","5:a","-t",str(DURATION),"-r",str(FPS),"-c:v","libx264","-preset","veryfast","-crf","31","-pix_fmt","yuv420p","-c:a","aac","-b:a","48k","-ac","2","-movflags","+faststart",str(OUT/f"{slug}.mp4")]
    print("Generating",slug,flush=True);subprocess.run(cmd,check=True);wav.unlink()
    for p in pics:p.unlink()
    print("Created",slug,(OUT/f"{slug}.mp4").stat().st_size,"bytes",flush=True)
print("Complete:",len(STORIES),"original five-minute offline MP4 films.")
