(function () {
  'use strict';
  var KEY = 'ardyil_v30_quest_v1';
  var LEVELS = [
    ['Salam dan Sapaan','Garden of Words',"How's it going?",'Apa kabar? / Bagaimana kabarmu?','How are you today?'],
    ['Meminta Bantuan','Garden of Words','Could you help me?','Bisakah kamu membantuku?','Could you help me with this exercise?'],
    ['Memberi Saran','Garden of Words','You should try again.','Sebaiknya kamu mencoba lagi.','You should read the sentence aloud.'],
    ['Pilihan Lebih Baik','Garden of Words','It would be better to wait.','Akan lebih baik untuk menunggu.','It would be better to ask first.'],
    ['Menyampaikan Pendapat','Garden of Words','I think this is a good idea.','Menurutku ini ide yang bagus.','I think we can learn together.'],
    ['Kebiasaan Harian','Daily Steps','I usually study at night.','Aku biasanya belajar pada malam hari.','I usually review new words after dinner.'],
    ['Sedang Berlangsung','Daily Steps','She is reading a book.','Dia sedang membaca sebuah buku.','She is reading an English story.'],
    ['Pengalaman Kemarin','Daily Steps','We visited the museum yesterday.','Kami mengunjungi museum kemarin.','We learned many new things there.'],
    ['Rencana Masa Depan','Daily Steps','I am going to practice tomorrow.','Aku akan berlatih besok.','I am going to practice speaking tomorrow.'],
    ['Membandingkan','Daily Steps','This lesson is easier than the last one.','Pelajaran ini lebih mudah daripada pelajaran sebelumnya.','This puzzle is harder than the first one.'],
    ['Sudah Selesai','Grammar Grove','I have already finished my homework.','Aku sudah menyelesaikan pekerjaan rumahku.','She has already called her teacher.'],
    ['Durasi Kegiatan','Grammar Grove','I have been learning English for a year.','Aku telah belajar bahasa Inggris selama setahun.','They have been waiting for ten minutes.'],
    ['Mempersiapkan Ujian','Grammar Grove','The students are preparing for an exam.','Para siswa sedang mempersiapkan ujian.','The students are reviewing their notes.'],
    ['Kalimat Pasif','Grammar Grove','The meeting has been postponed.','Rapat telah ditunda.','The class has been moved to Friday.'],
    ['Menghubungkan Ide','Grammar Grove','Although it was raining, we continued walking.','Meskipun sedang hujan, kami terus berjalan.','Although the task was difficult, she finished it.'],
    ['Bertanya Arah','World Explorer','Where is the nearest station?','Di mana stasiun terdekat?','Where is the nearest bus stop?'],
    ['Memesan Minuman','World Explorer','Could I have a glass of water?','Bolehkah saya minta segelas air?','Could I have the menu, please?'],
    ['Memesan Kamar','World Explorer','I would like to book a room.','Saya ingin memesan kamar.','I would like to book a room for two nights.'],
    ['Cuaca Berubah','World Explorer','The weather is getting warmer.','Cuaca semakin hangat.','The days are getting longer.'],
    ['Menjaga Lingkungan','World Explorer','Please keep the environment clean.','Tolong jaga lingkungan tetap bersih.','We should reduce plastic waste.'],
    ['Mencari Solusi','Bright Thinkers',"Let's look for another solution.",'Mari kita mencari solusi lain.','Let’s discuss the problem together.'],
    ['Setuju dengan Saran','Bright Thinkers','I agree with your suggestion.','Saya setuju dengan saranmu.','I agree with your point of view.'],
    ['Sebab dan Akibat','Bright Thinkers','The event was canceled because of the rain.','Acara itu dibatalkan karena hujan.','The game stopped because of the weather.'],
    ['Kalimat Pengandaian','Bright Thinkers','If I had more time, I would read more.','Jika punya lebih banyak waktu, saya akan membaca lebih banyak.','If I were free, I would join you.'],
    ['Passive Voice','Bright Thinkers','The report was written by the team.','Laporan itu ditulis oleh tim.','The letters were sent yesterday.'],
    ['Reported Speech','English Mastery','She said that she was busy.','Dia mengatakan bahwa dia sedang sibuk.','He said that he needed more time.'],
    ['Menyatakan Tujuan','English Mastery','I study English so that I can communicate.','Saya belajar bahasa Inggris agar bisa berkomunikasi.','She saves money so that she can buy a book.'],
    ['Menyatakan Kemungkinan','English Mastery','It might rain later this afternoon.','Mungkin akan hujan nanti sore.','The train could arrive a little late.'],
    ['Menyimpulkan','English Mastery','Based on the evidence, this is the best choice.','Berdasarkan bukti, ini pilihan terbaik.','In conclusion, teamwork makes the task easier.'],
    ['Ungkapan Formal','English Mastery','I would appreciate your response.','Saya akan sangat menghargai tanggapan Anda.','I would appreciate your help with this matter.']
  ];
  if (LEVELS.length !== 30) throw new Error('ARDYIL 30.0 harus memiliki tepat 30 level.');
  var QUIZ = [
    ['What does “You should try again” mean?','Sebaiknya kamu mencoba lagi.',['Aku sudah mencobanya.','Jangan mencoba sekarang.','Aku akan pergi besok.']],
    ['Choose the correct meaning of “although”.','meskipun',['karena','sebelum','sehingga']],
    ['What does “I agree with your suggestion” mean?','Saya setuju dengan saranmu.',['Saya menolak saranmu.','Saya belum mendengarnya.','Saya punya saran baru.']]
  ];
  var WORDS = [['thoughtful','penuh pertimbangan'],['reliable','dapat diandalkan'],['meanwhile','sementara itu'],['eventually','akhirnya'],['improve','meningkatkan']];
  var STORY = [
    ['A New Friend','Mia meets a new student and introduces herself.','Mia bertemu siswa baru dan memperkenalkan dirinya.'],
    ['A Helpful Idea','They share ideas and work together on a class project.','Mereka berbagi ide dan bekerja sama dalam proyek kelas.'],
    ['A Good Beginning','They become friends and decide to keep learning English.','Mereka menjadi teman dan memutuskan terus belajar bahasa Inggris.']
  ];
  function dateKey() { var d = new Date(); return d.getFullYear()+'-'+(d.getMonth()+1)+'-'+d.getDate(); }
  function fresh() { return {day:dateKey(),coins:0,xp:0,unlocked:1,completed:[],daily:{},quiz:0,words:[],scene:0}; }
  function readState() {
    var s = fresh();
    try { s = Object.assign(s, JSON.parse(localStorage.getItem(KEY) || '{}')); } catch (e) {}
    s.coins=Math.max(0,Number(s.coins)||0); s.xp=Math.max(0,Number(s.xp)||0);
    s.unlocked=Math.max(1,Math.min(30,Number(s.unlocked)||1));
    s.completed=Array.isArray(s.completed)?s.completed.filter(function(n){return Number.isInteger(n)&&n>=0&&n<30;}):[];
    s.words=Array.isArray(s.words)?s.words.filter(function(n){return Number.isInteger(n)&&n>=0&&n<WORDS.length;}):[];
    s.daily=s.daily&&typeof s.daily==='object'?s.daily:{};
    if(s.day!==dateKey()){s.day=dateKey();s.daily={};s.quiz=0;s.words=[];s.scene=0;}
    s.quiz=Math.max(0,Math.min(QUIZ.length,Number(s.quiz)||0)); s.scene=Math.max(0,Math.min(STORY.length,Number(s.scene)||0));
    return s;
  }
  var state=readState(), activeLevel=-1;
  function save(){try{localStorage.setItem(KEY,JSON.stringify(state));}catch(e){message('Penyimpanan perangkat tidak tersedia.');}}
  function el(id){return document.getElementById(id);}
  function esc(s){return String(s).replace(/[&<>"']/g,function(c){return {'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[c];});}
  function message(s){var f=el('v30-feedback-global');if(!f){f=document.createElement('p');f.id='v30-feedback-global';f.className='v30-feedback';el('v30-quest-hub').appendChild(f);}f.textContent=s;}
  function update(){
    el('v30-coins').textContent=state.coins;el('v30-xp').textContent=state.xp;
    el('v30-level-now').textContent=state.unlocked;el('v30-level-count').textContent=state.completed.length+' / 30 selesai';
    el('v30-unlocked-count').textContent=state.unlocked+' level terbuka';el('v30-level-fill').style.width=(state.completed.length/30*100)+'%';
    var u=el('v30-unlock-next');u.disabled=state.unlocked>=30;u.textContent=state.unlocked>=30?'Semua level telah terbuka ✓':'Buka level '+(state.unlocked+1)+' · 20 koin';
    [['quiz','Kuis 3 pertanyaan · 12 koin'],['vocab','Review 5 kosakata · 10 koin'],['story','Baca episode mini · 10 koin']].forEach(function(x){var b=document.querySelector('[data-v30-start="'+x[0]+'"]'),t=el('v30-'+x[0]+'-status');b.disabled=!!state.daily[x[0]];b.textContent=b.disabled?'Selesai ✓':'Mulai';t.textContent=b.disabled?'Selesai · hadiah diterima':x[1];});
  }
  function reward(id,c,x){if(state.daily[id])return;state.daily[id]=true;state.coins+=c;state.xp+=x;save();update();message('Misi selesai! +'+c+' koin dan +'+x+' XP.');}
  function header(s){return '<div class="v30-stage-top"><b>'+esc(s)+'</b><button class="v30-close" data-close type="button">Tutup ×</button></div>';}
  function showStage(html){var s=el('v30-stage');s.hidden=false;s.innerHTML=html;s.scrollIntoView({behavior:'smooth',block:'nearest'});}
  function startMission(id){if(state.daily[id]){message('Misi ini sudah selesai hari ini.');return;}if(id==='quiz')drawQuiz();if(id==='vocab')drawWords();if(id==='story')drawStory();}
  function drawQuiz(){var i=state.quiz;if(i>=QUIZ.length){showStage(header('Kuis harian')+'<h4>Hebat! Semua jawaban benar.</h4><p>Hadiah sudah masuk ke dompetmu.</p>');return;}var q=QUIZ[i],opts=[q[1]].concat(q[2]);showStage(header('Kuis harian · '+(i+1)+' / 3')+'<h4>'+esc(q[0])+'</h4><div class="v30-options">'+opts.map(function(o){return '<button type="button" data-quiz="'+esc(o)+'">'+esc(o)+'</button>';}).join('')+'</div><div class="v30-feedback" id="v30-stage-feedback">Pilih satu jawaban.</div>');}
  function drawWords(){showStage(header('Kebun kosakata · '+state.words.length+' / 5')+'<h4>Pelajari lima kata ini</h4><div class="v30-flash">'+WORDS.map(function(w,i){var seen=state.words.indexOf(i)>=0;return '<article class="v30-flash-card"><b>'+esc(w[0])+'</b><p '+(seen?'':'hidden')+'>'+esc(w[1])+'</p><button type="button" data-word="'+i+'" '+(seen?'disabled':'')+'>'+(seen?'Sudah dipelajari ✓':'Tampilkan arti')+'</button></article>';}).join('')+'</div><div class="v30-feedback">Buka arti setiap kosakata untuk menyelesaikan misi.</div>');}
  function drawStory(){var i=state.scene;if(i>=STORY.length){showStage(header('Episode mini')+'<h4>Episode selesai!</h4><p>Hadiah misi sudah diterima.</p>');return;}var s=STORY[i];showStage(header('Episode mini · adegan '+(i+1)+' / 3')+'<div class="v30-story-scene"><span>ARDYIL ORIGINAL</span><b>'+esc(s[0])+'</b><p>'+esc(s[1])+'</p><p class="v30-translation">'+esc(s[2])+'</p></div><div style="margin-top:10px"><button type="button" class="v30-primary" data-story-next>'+(i===2?'Selesaikan episode':'Adegan berikutnya →')+'</button></div>');}
  function drawMap(){el('v30-level-map').innerHTML=LEVELS.map(function(l,i){var done=state.completed.indexOf(i)>=0,locked=i>=state.unlocked;return '<button type="button" class="v30-level-node '+(done?'done ':'')+(locked?'locked ':'')+(i===state.unlocked-1?'current':'')+'" data-level="'+i+'"><b>'+(done?'✓':locked?'🔒':'LEVEL '+(i+1))+'</b><small>'+esc(l[0])+'</small></button>';}).join('');}
  function openLevel(i){if(i>=state.unlocked){message('Level '+(i+1)+' masih terkunci. Gunakan 20 koin untuk membukanya.');return;}activeLevel=i;el('v30-level-map').hidden=true;el('v30-map-toggle').textContent='Lihat peta ↓';el('v30-map-toggle').setAttribute('aria-expanded','false');var l=LEVELS[i],opts=[l[3],LEVELS[(i+4)%30][3],LEVELS[(i+11)%30][3],LEVELS[(i+19)%30][3]];opts=[...new Set(opts)];while(opts.length<4)opts.push(LEVELS[(i+opts.length+7)%30][3]);showStage(header('LEVEL '+(i+1)+' · '+l[1])+'<h4>'+esc(l[0])+'</h4><div class="v30-phrase">'+esc(l[2])+'</div><div class="v30-translation">'+esc(l[3])+'</div><p><b>Contoh:</b> '+esc(l[4])+'</p><p class="v30-tip">Gunakan ungkapan ini dalam latihan percakapanmu.</p><h4 style="font-size:13px">Pilih terjemahan yang benar.</h4><div class="v30-options">'+opts.map(function(o){return '<button type="button" data-level-answer="'+esc(o)+'">'+esc(o)+'</button>';}).join('')+'</div><div id="v30-stage-feedback" class="v30-feedback">Jawaban benar pertama memberi 8 koin + 15 XP.</div>');}
  function unlock(){if(state.unlocked>=30){message('Semua level sudah terbuka!');return;}if(state.coins<20){message('Koin belum cukup. Selesaikan misi harian untuk mendapatkan koin.');return;}state.coins-=20;state.unlocked++;save();update();drawMap();message('Level '+state.unlocked+' berhasil dibuka!');}
  function init(){var hub=el('v30-quest-hub');if(!hub||hub.dataset.ready)return;hub.dataset.ready='1';update();drawMap();
    el('v30-missions').addEventListener('click',function(e){var b=e.target.closest('[data-v30-start]');if(b)startMission(b.dataset.v30Start);});
    el('v30-map-toggle').addEventListener('click',function(){var m=el('v30-level-map');m.hidden=!m.hidden;this.textContent=m.hidden?'Lihat peta ↓':'Tutup peta ↑';this.setAttribute('aria-expanded',String(!m.hidden));if(!m.hidden)drawMap();});
    el('v30-unlock-next').addEventListener('click',unlock);
    el('v30-level-map').addEventListener('click',function(e){var b=e.target.closest('[data-level]');if(b)openLevel(Number(b.dataset.level));});
    el('v30-stage').addEventListener('click',function(e){var b=e.target.closest('button');if(!b)return;if(b.hasAttribute('data-close')){el('v30-stage').hidden=true;return;}
      if(b.hasAttribute('data-quiz')){var q=QUIZ[state.quiz];if(b.dataset.quiz===q[1]){state.quiz++;save();if(state.quiz>=3)reward('quiz',12,20);drawQuiz();}else{el('v30-stage-feedback').textContent='Belum tepat. Coba lagi, koin tidak berkurang.';}return;}
      if(b.hasAttribute('data-word')){var i=Number(b.dataset.word);if(state.words.indexOf(i)<0)state.words.push(i);save();drawWords();if(state.words.length>=5)reward('vocab',10,15);return;}
      if(b.hasAttribute('data-story-next')){state.scene++;save();if(state.scene>=3)reward('story',10,15);drawStory();return;}
      if(b.hasAttribute('data-level-answer')){if(b.dataset.levelAnswer===LEVELS[activeLevel][3]){if(state.completed.indexOf(activeLevel)<0){state.completed.push(activeLevel);state.coins+=8;state.xp+=15;save();update();drawMap();el('v30-stage-feedback').textContent='Benar! +8 koin dan +15 XP.';}else{el('v30-stage-feedback').textContent='Benar! Hadiah level ini sudah pernah diterima.';}Array.from(el('v30-stage').querySelectorAll('[data-level-answer]')).forEach(function(x){x.disabled=true;});}else{el('v30-stage-feedback').textContent='Belum tepat. Baca kembali ungkapan dan artinya.';}return;}
    });
    hub.querySelector('.v30-billing-status').title='Pembelian belum aktif; produk dan verifikasi resmi Google Play belum dikonfigurasi.';
  }
  if(document.readyState==='loading')document.addEventListener('DOMContentLoaded',init,{once:true});else init();
})();