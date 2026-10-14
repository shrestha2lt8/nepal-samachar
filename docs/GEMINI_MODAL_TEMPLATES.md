# Gemini AI Modal Dialogs & Bottom Sheet Templates

This document specifies token-efficient schemas, UI contracts, and prompt templates for in-app Gemini AI modals and bottom sheets.

---

## 1. AI Quick Summary Modal (`AiSummaryBottomSheet`)

### Purpose
Allows users to tap an "AI संक्षेप" (AI Summary) button in the article reader or news feed to view a high-density 3-point summary without reading the whole page.

### Token Budget
- **System Prompt:** ~45 tokens
- **Input Text:** ~300 - 600 tokens (truncated to first 1200 characters)
- **Output:** ~80 - 120 tokens
- **Total per call:** < 800 tokens

### Modal State Model
```kotlin
sealed class AiSummaryUiState {
    object Idle : AiSummaryUiState()
    object Loading : AiSummaryUiState()
    data class Success(
        val bullets: List<String>,
        val readTimeSeconds: Int,
        val sentiment: String
    ) : AiSummaryUiState()
    data class Error(val message: String) : AiSummaryUiState()
}
```

### System Instruction
```
Summarize the following Nepali news text into exactly 3 bullet points in formal Nepali. Each point must be under 20 words. No intro or outro text.
```

---

## 2. Multi-Source Fact Check & Comparison Modal (`AiFactCheckModal`)

### Purpose
When multiple portals (e.g. Onlinekhabar, Setopati, Ratopati) report on the same breaking news event, this modal synthesizes all perspectives and highlights consensus facts.

### Token Budget
- **System Prompt:** ~55 tokens
- **Input Text:** ~500 tokens (titles + first 2 sentences per source)
- **Output:** ~150 tokens
- **Total per call:** < 750 tokens

### Structured Response Schema (JSON Mode)
```json
{
  "consensusSummary": "सबै समाचार स्रोतहरू सहमत भएका मुख्य तथ्य...",
  "differingDetails": [
    "स्रोत अनुसार फरक परेका विवरण वा दाबीहरू..."
  ],
  "sourceCount": 3
}
```

---

## 3. 60-Second Radio News Bulletin Modal (`AiAudioBulletinModal`)

### Purpose
Generates a broadcast-ready radio news script for users listening to FM radio who want a quick synthesized audio update.

### Token Budget
- **System Prompt:** ~50 tokens
- **Input Text:** ~400 tokens (top 5 headline strings)
- **Output:** ~140 tokens
- **Total per call:** < 600 tokens

### System Instruction
```
You are a Nepali radio news anchor. Convert the provided top 5 news headlines into a smooth, professional 60-second radio news bulletin in Nepali. Use natural broadcast transitions.
```

---

## 4. Token Reduction Best Practices for Gemini Modals
1. **Pre-clean HTML**: Strip all `<p>`, `<a>`, and `<div>` tags before passing text to the model.
2. **Character Budget**: Truncate article body to 1500 characters max (`article.content?.take(1500)`).
3. **Suppress Greetings**: Explicitly prompt `No intro, no conclusion, direct answer only`.
4. **Use `gemini-1.5-flash` or `gemini-2.0-flash`**: Highest throughput, lowest latency, and minimal cost for mobile modal interactions.
