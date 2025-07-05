const puppeteer = require('puppeteer');
const fs = require('fs');
const fetch = require('node-fetch');
const FormData = require('form-data');

const meetingId = process.argv[2];
const url = `http://localhost:8080/?room=${meetingId}`;

(async () => {
  const browser = await puppeteer.launch({
    headless: false,
    args: [
      '--use-fake-ui-for-media-stream',
      '--no-sandbox',
      '--disable-setuid-sandbox'
    ]
  });

  const page = await browser.newPage();
  await page.goto(url, { waitUntil: 'networkidle2' });

  // Join room
  await page.waitForSelector('#roomName');
  await page.type('#roomName', meetingId);
  await page.click('#btnConnect');

  console.log("🤖 Bot joined meeting:", meetingId);

  // Wait for a remote video element to appear
  await page.waitForFunction(() => {
  const videos = [...document.querySelectorAll('video')];
    console.log("🎬 Total videos on page:", videos.length);

    const hasRemote = videos.some(v => v.id !== 'localVideo');
     console.log("👀 Has remote video?", hasRemote);
     return hasRemote;
  }, { timeout: 60000 });

  console.log("🎥 Remote participant joined. Starting recording...");

  await page.exposeFunction('saveAudio', async (base64) => {
  console.log("inside save audio");
    const buffer = Buffer.from(base64, 'base64'); // Step 1: Decode base64 to binary
    console.log(buffer);
    const timestamp = Date.now();
    //fs.writeFileSync(`bot-recording-${timestamp}.webm`, buffer);
    const form = new FormData();
    form.append('file',buffer,{
        filename:`bot-recording-${timestamp}.webm`,
        contentType: 'audio/webm'
    });
    form.append('meetId', meetingId);
    console.log(form);

     try {
    const res = await fetch("http://192.168.2.4:9000/api/audiototext", {
      method: 'POST',
      body: form,
      headers: form.getHeaders()
    });

   const text = await res.text();
     console.log("✅ Audio sent. Server responded with:", text);
    }
    catch (err) {
          console.error("❌ Failed to send audio:", err.message);
        }
    console.log("📥 Audio transfer to audio-to-transcript service");
  });

await page.evaluate(() => {
  const remoteVideo = [...document.querySelectorAll('video')].find(v => v.id !== 'localVideo');
  remoteVideo.muted = false;
  remoteVideo.play();

  const stream = remoteVideo.srcObject;
  if (!stream) {
      console.error("❌ remoteVideo.srcObject is null");
      return;
    }
  const audioTracks = stream.getAudioTracks();

  if (audioTracks.length === 0) {
    console.error("❌ No audio tracks found in remote video.");
    return;
  }

  const audioStream = new MediaStream(audioTracks);
  const recorder = new MediaRecorder(audioStream);
  let chunks = [];

  recorder.ondataavailable = (e) => chunks.push(e.data);

  recorder.onstop = async () => {
  console.log("inside recorder.onstop");
    const blob = new Blob(chunks, { type: 'audio/webm' });
    console.log(blob);
    const reader = new FileReader();
    reader.onloadend = () => {
    console.log("before");
      const base64data = reader.result.split(',')[1];
      window.saveAudio(base64data);
      console.log("after");
    };
    console.log("befor readAsDataURL");
    reader.readAsDataURL(blob);
    console.log("after readAsDataURL");
  };

  recorder.start();
  console.log("⏺️ Audio-only recording started");

  setTimeout(() => {
    recorder.stop();
    console.log("⏹️ Recording stopped");
  }, 1 * 60 * 1000); // 3 minutes
});


  // Wait for recording to finish
  await new Promise(resolve => setTimeout(resolve, 1 * 60 * 1000 + 5000));
  await browser.close();
})();