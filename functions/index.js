const functions = require("firebase-functions");
const admin = require("firebase-admin");
const axios = require("axios");

admin.initializeApp();

const daftarKota = {
  "Samarinda": "7582584",
  "Jakarta": "1642911",
  "Surabaya": "1625822"
};

exports.syncJadwalSholat = functions.pubsub.schedule("every 24 hours").onRun(async (context) => {
  const baseUrl = "https://api.myquran.com/v1/sholat/jadwal/";
  const tanggal = new Date().toISOString().split("T")[0];

  for (const [kota, kode] of Object.entries(daftarKota)) {
    try {
      const response = await axios.get(`${baseUrl}${kode}/${tanggal}`);
      const jadwal = response.data.data.jadwal;

      await admin.database().ref(`jadwal_sholat/${kota}`).set({
        subuh: jadwal.subuh,
        dzuhur: jadwal.dzuhur,
        ashar: jadwal.ashar,
        maghrib: jadwal.maghrib,
        isya: jadwal.isya
      });

      console.log(`✅ Jadwal ${kota} berhasil disimpan`);
    } catch (error) {
      console.error(`❌ Gagal sinkronisasi ${kota}`, error);
    }
  }

  return null;
});
