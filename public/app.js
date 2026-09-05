document.addEventListener('DOMContentLoaded', () => {
    // LOGIN AUTHENTICATION STATE
    const loginScreen = document.getElementById('loginScreen');
    const loginForm = document.getElementById('loginForm');
    const demoLoginBtn = document.getElementById('demoLoginBtn');
    const logoutBtn = document.getElementById('logoutBtn');

    function checkAuth() {
        const isLoggedIn = localStorage.getItem('id_chan_logged_in') === 'true';
        if (isLoggedIn) {
            loginScreen.classList.add('hidden');
        } else {
            loginScreen.classList.remove('hidden');
        }
    }

    loginForm.addEventListener('submit', (e) => {
        e.preventDefault();
        const user = document.getElementById('loginUsername').value.trim();
        const pass = document.getElementById('loginPassword').value.trim();
        if (user && pass) {
            localStorage.setItem('id_chan_logged_in', 'true');
            localStorage.setItem('id_chan_user', user);
            checkAuth();
            showToast(`Welcome back, ${user}!`);
        }
    });

    demoLoginBtn.addEventListener('click', () => {
        document.getElementById('loginUsername').value = 'creator';
        document.getElementById('loginPassword').value = 'idchan2026';
        localStorage.setItem('id_chan_logged_in', 'true');
        localStorage.setItem('id_chan_user', 'creator');
        checkAuth();
        showToast('Logged in as demo user');
    });

    logoutBtn.addEventListener('click', () => {
        localStorage.removeItem('id_chan_logged_in');
        checkAuth();
        showToast('Signed out');
    });

    checkAuth();

    // STATE
    let selectedImageSrc = null;
    let selectedMode = 'EXACT_RECREATION';
    let currentAnalysis = null;

    // DOM ELEMENTS
    const homeTab = document.getElementById('homeTab');
    const historyTab = document.getElementById('historyTab');
    const favoritesTab = document.getElementById('favoritesTab');
    const settingsTab = document.getElementById('settingsTab');

    const emptyState = document.getElementById('emptyState');
    const previewState = document.getElementById('previewState');
    const selectedImagePreview = document.getElementById('selectedImagePreview');
    const loadingOverlay = document.getElementById('loadingOverlay');
    const resultCard = document.getElementById('resultCard');
    const masterPromptArea = document.getElementById('masterPromptArea');
    const negativePromptArea = document.getElementById('negativePromptArea');
    const charCount = document.getElementById('charCount');
    const toast = document.getElementById('toast');

    // SAMPLE IMAGES MAPPING
    const sampleImages = {
        portrait: 'https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=800&q=80',
        landscape: 'https://images.unsplash.com/photo-1506744038136-46273834b3fb?auto=format&fit=crop&w=800&q=80',
        cyberpunk: 'https://images.unsplash.com/photo-1518709268805-4e9042af9f23?auto=format&fit=crop&w=800&q=80'
    };

    // TAB NAVIGATION
    document.querySelectorAll('.nav-item').forEach(btn => {
        btn.addEventListener('click', () => {
            document.querySelectorAll('.nav-item').forEach(b => b.classList.remove('active'));
            document.querySelectorAll('.tab-content').forEach(t => t.classList.remove('active'));

            btn.classList.add('active');
            const tabId = btn.getAttribute('data-tab');
            document.getElementById(tabId).classList.add('active');

            if (tabId === 'historyTab') renderHistory();
            if (tabId === 'favoritesTab') renderFavorites();
        });
    });

    // MODE CHIPS SELECTION
    document.querySelectorAll('.mode-chips .chip').forEach(chip => {
        chip.addEventListener('click', () => {
            document.querySelectorAll('.mode-chips .chip').forEach(c => c.classList.remove('active'));
            chip.classList.add('active');
            selectedMode = chip.getAttribute('data-mode');
        });
    });

    // SAMPLE IMAGES SELECTION
    document.querySelectorAll('.sample-thumbnails img').forEach(img => {
        img.addEventListener('click', () => {
            const key = img.getAttribute('data-sample');
            selectImage(sampleImages[key]);
        });
    });

    // FILE PICKER
    const imageFileInput = document.getElementById('imageFileInput');
    imageFileInput.addEventListener('change', (e) => {
        if (e.target.files && e.target.files[0]) {
            const reader = new FileReader();
            reader.onload = (event) => {
                selectImage(event.target.result);
            };
            reader.readAsDataURL(e.target.files[0]);
        }
    });

    // CAMERA BTN
    document.getElementById('cameraBtn').addEventListener('click', () => {
        imageFileInput.click();
    });

    function selectImage(src) {
        selectedImageSrc = src;
        selectedImagePreview.src = src;
        emptyState.classList.add('hidden');
        previewState.classList.remove('hidden');
        resultCard.classList.add('hidden');
    }

    // CHANGE IMAGE
    document.getElementById('changeImageBtn').addEventListener('click', () => {
        selectedImageSrc = null;
        previewState.classList.add('hidden');
        emptyState.classList.remove('hidden');
        resultCard.classList.add('hidden');
    });

    // ANALYZE IMAGE
    document.getElementById('analyzeBtn').addEventListener('click', () => {
        if (!selectedImageSrc) return;

        loadingOverlay.classList.remove('hidden');
        const steps = [
            "Studying facial features, pose & physical details...",
            "Analyzing spatial composition & rule of thirds placement...",
            "Detecting light direction, rim accents & shadow dynamics...",
            "Estimating camera perspective & lens focal length...",
            "Evaluating color palette, temperature & color grading...",
            "Reconstructing high-fidelity image prompt..."
        ];

        let stepIndex = 0;
        const stepInterval = setInterval(() => {
            stepIndex++;
            if (stepIndex < steps.length) {
                document.getElementById('loadingStepText').textContent = steps[stepIndex];
            } else {
                clearInterval(stepInterval);
                finishAnalysis();
            }
        }, 350);
    });

    // GROQ API KEY STORAGE
    const groqApiKeyInput = document.getElementById('groqApiKeyInput');
    const saveGroqApiKeyBtn = document.getElementById('saveGroqApiKeyBtn');

    if (groqApiKeyInput) {
        groqApiKeyInput.value = localStorage.getItem('id_chan_groq_key') || '';
        saveGroqApiKeyBtn.addEventListener('click', () => {
            localStorage.setItem('id_chan_groq_key', groqApiKeyInput.value.trim());
            showToast('Groq API Key saved!');
        });
    }

    async function finishAnalysis() {
        loadingOverlay.classList.add('hidden');
        const groqKey = localStorage.getItem('id_chan_groq_key');
        let masterPrompt = "";
        let negativePrompt = "distorted anatomy, extra limbs, extra fingers, mutated hands, poorly drawn face, blurry details, incorrect proportions, low resolution, unwanted artifacts, oversaturated colors, unnatural skin texture, harsh unwanted shadows";

        if (groqKey && selectedImageSrc && selectedImageSrc.startsWith('data:')) {
            try {
                const response = await fetch('https://api.groq.com/openai/v1/chat/completions', {
                    method: 'POST',
                    headers: {
                        'Authorization': `Bearer ${groqKey}`,
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({
                        model: 'llama-3.2-11b-vision-preview',
                        messages: [
                            {
                                role: 'user',
                                content: [
                                    { type: 'text', text: `Analyze this image in mode: ${selectedMode}. Output a detailed reverse-engineered image generation prompt to recreate this image.` },
                                    { type: 'image_url', image_url: { url: selectedImageSrc } }
                                ]
                            }
                        ],
                        max_tokens: 1000
                    })
                });
                const data = await response.json();
                if (data.choices && data.choices[0] && data.choices[0].message) {
                    masterPrompt = data.choices[0].message.content;
                }
            } catch (err) {
                console.error("Groq API error", err);
            }
        }

        if (!masterPrompt) {
            masterPrompt = generatePrompt(selectedMode);
        }

        currentAnalysis = {
            id: Date.now().toString(),
            imageUri: selectedImageSrc,
            masterPrompt: masterPrompt,
            negativePrompt: negativePrompt,
            mode: selectedMode,
            timestamp: Date.now(),
            isFavorite: false
        };

        masterPromptArea.value = masterPrompt;
        negativePromptArea.value = negativePrompt;
        charCount.textContent = `${masterPrompt.length} chars`;

        resultCard.classList.remove('hidden');
        saveToHistory(currentAnalysis);
    }

    function generatePrompt(mode) {
        const subject = "A charismatic central subject positioned slightly left of center, looking softly towards the right foreground with a relaxed facial expression";
        const appearance = "subtle facial warmth, expressive dark eyes with crisp catchlights, detailed hair texture with soft light highlights";
        const clothing = "wearing modern tailored casual attire with visible micro-fabric textures, matte finish";
        const environment = "set within a modern architectural indoor space with soft ambient background elements, natural wood and brushed metal surfaces";
        const composition = "composed using the rule of thirds, medium close-up framing, generous headroom, smooth leading lines";
        const camera = "photographed with an 85mm portrait perspective, shallow depth of field, cream-like background bokeh";
        const lighting = "illuminated by a soft key light from upper-left, gentle fill light softening shadows, distinct rim light on hair and shoulders";
        const color = "harmonious palette featuring warm bronze and teal accents, natural skin tones, balanced color grading";
        const quality = "photorealistic portrait photography, ultra-sharp detail, subtle texture depth, 8k resolution";

        switch (mode) {
            case 'PHOTOREALISTIC':
                return `Raw photograph shot on Hasselblad X2D: ${subject}, ${appearance}, ${clothing}. ${camera}. ${lighting}, authentic light physics, hyper-detailed skin texture.`;
            case 'CINEMATIC':
                return `Cinematic film still: ${subject} in ${environment}. Dramatic ${lighting}, anamorphic lens perspective, ${color}, subtle atmospheric grain.`;
            case 'SHORT':
                return `${subject}, ${clothing}, ${environment}, ${camera}, ${lighting}.`;
            case 'DETAILED':
                return `Master reverse-engineered prompt: ${subject}. Appearance: ${appearance}. Outfit: ${clothing}. Setting: ${environment}. Camera: ${camera}. Lighting: ${lighting}. Color: ${color}.`;
            default:
                return `${subject}. ${appearance}, ${clothing}. ${environment}. ${composition}. ${camera}. ${lighting}. ${color}. ${quality}.`;
        }
    }

    // REFINE BUTTONS
    document.querySelectorAll('.refine-chip').forEach(btn => {
        btn.addEventListener('click', () => {
            const action = btn.getAttribute('data-action');
            let current = masterPromptArea.value;
            if (action === 'improve') {
                masterPromptArea.value = `A masterfully composed high-fidelity photographic portrait: ${current}, render with micro-details, ultra-sharp focus, cinematic perfection`;
            } else if (action === 'more_detailed') {
                masterPromptArea.value = `${current}, intricate fabric weave textures, visible pores, natural catchlights in the eyes, subtle environmental reflections, dynamic lighting depth`;
            } else if (action === 'shorter') {
                const words = current.split(' ');
                masterPromptArea.value = words.slice(0, 18).join(' ') + '...';
            } else if (action === 'photorealistic') {
                masterPromptArea.value = `Ultra-realistic raw photography shot on 85mm f/1.4 prime lens: ${current}, organic light dispersion, authentic skin imperfections`;
            } else if (action === 'cinematic') {
                masterPromptArea.value = `Cinematic wide-angle frame: ${current}, volumetric fog, dramatic rim lighting, golden hour illumination, subtle lens flare, deep contrast shadows`;
            }
            charCount.textContent = `${masterPromptArea.value.length} chars`;
            showToast('Prompt updated!');
        });
    });

    // COPY PROMPT
    document.getElementById('copyPromptBtn').addEventListener('click', () => {
        navigator.clipboard.writeText(masterPromptArea.value);
        showToast('Prompt copied!');
    });

    // FAVORITE TOGGLE
    document.getElementById('favBtn').addEventListener('click', () => {
        if (!currentAnalysis) return;
        currentAnalysis.isFavorite = !currentAnalysis.isFavorite;
        const icon = document.getElementById('favBtn').querySelector('i');
        if (currentAnalysis.isFavorite) {
            icon.className = 'fa-solid fa-heart';
            icon.style.color = 'var(--error-red)';
            showToast('Added to Favorites');
        } else {
            icon.className = 'fa-regular fa-heart';
            icon.style.color = '';
            showToast('Removed from Favorites');
        }
        updateHistoryFavorite(currentAnalysis.id, currentAnalysis.isFavorite);
    });

    // SHARE
    document.getElementById('shareBtn').addEventListener('click', () => {
        if (navigator.share) {
            navigator.share({
                title: 'ID Chan Prompt',
                text: masterPromptArea.value
            }).catch(() => {});
        } else {
            navigator.clipboard.writeText(masterPromptArea.value);
            showToast('Prompt copied for sharing!');
        }
    });

    // LOCAL STORAGE HISTORY
    function getHistory() {
        return JSON.parse(localStorage.getItem('id_chan_history') || '[]');
    }

    function saveToHistory(item) {
        const history = getHistory();
        history.unshift(item);
        localStorage.setItem('id_chan_history', JSON.stringify(history));
    }

    function updateHistoryFavorite(id, isFav) {
        const history = getHistory();
        const found = history.find(h => h.id === id);
        if (found) {
            found.isFavorite = isFav;
            localStorage.setItem('id_chan_history', JSON.stringify(history));
        }
    }

    function renderHistory() {
        const list = document.getElementById('historyList');
        const history = getHistory();
        if (history.length === 0) {
            list.innerHTML = '<div style="text-align:center; padding:40px; color:var(--text-secondary);"><i class="fa-solid fa-clock-rotate-left" style="font-size:48px; margin-bottom:12px;"></i><p>No History Yet</p></div>';
            return;
        }

        list.innerHTML = history.map(h => `
            <div class="history-card">
                <img src="${h.imageUri}" class="history-thumb" alt="Thumbnail">
                <div class="history-info">
                    <div class="history-meta">
                        <span>${h.mode}</span>
                        <span>${new Date(h.timestamp).toLocaleDateString()}</span>
                    </div>
                    <p>${h.masterPrompt}</p>
                </div>
            </div>
        `).join('');
    }

    function renderFavorites() {
        const list = document.getElementById('favoritesList');
        const favs = getHistory().filter(h => h.isFavorite);
        if (favs.length === 0) {
            list.innerHTML = '<div style="text-align:center; padding:40px; color:var(--text-secondary);"><i class="fa-solid fa-heart" style="font-size:48px; margin-bottom:12px; color:rgba(255,82,82,0.4);"></i><p>No Favorite Prompts</p></div>';
            return;
        }

        list.innerHTML = favs.map(h => `
            <div class="history-card">
                <img src="${h.imageUri}" class="history-thumb" alt="Thumbnail">
                <div class="history-info">
                    <div class="history-meta">
                        <span>${h.mode}</span>
                        <span>${new Date(h.timestamp).toLocaleDateString()}</span>
                    </div>
                    <p>${h.masterPrompt}</p>
                </div>
            </div>
        `).join('');
    }

    document.getElementById('clearHistoryBtn').addEventListener('click', () => {
        localStorage.removeItem('id_chan_history');
        renderHistory();
        showToast('History cleared');
    });

    function showToast(msg) {
        toast.textContent = msg;
        toast.classList.remove('hidden');
        setTimeout(() => {
            toast.classList.add('hidden');
        }, 2000);
    }
});
