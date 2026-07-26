package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.*
import com.example.ui.viewmodel.HealthViewModel
import com.example.data.api.GeminiHealthAnalyzer
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class ChatMessage(
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    viewModel: HealthViewModel,
    onNavigateBack: () -> Unit
) {
    var textInput by remember { mutableStateOf("") }
    val chatMessages = remember {
        mutableStateListOf(
            ChatMessage("Hello! I am your AI Health Assistant. Ask me anything about your vitals, skin health, stress, or general wellness advice!", false)
        )
    }
    var documentContext by remember { mutableStateOf<String?>(null) }
    var isTyping by remember { mutableStateOf(false) }
    var typingMessage by remember { mutableStateOf("Assistant is typing...") }
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    val suggestionChips = listOf(
        "Explain my heart rate",
        "How do I reduce stress?",
        "Tips for better skin"
    )

    val context = androidx.compose.ui.platform.LocalContext.current

    val imagePickerLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.GetContent()
    ) { uri: android.net.Uri? ->
        uri?.let {
            chatMessages.add(ChatMessage("📸 Sent prescription photo for analysis. Analyzing details...", true))
            typingMessage = "AI is analyzing prescription image..."
            isTyping = true
            coroutineScope.launch {
                val analysisResult = GeminiHealthAnalyzer.analyzePrescription(context, it)
                documentContext = analysisResult // Save to RAG context
                isTyping = false
                
                // ChatGPT-style streaming typing animation for prescription analysis!
                val aiMsgIndex = chatMessages.size
                chatMessages.add(ChatMessage("", false))
                var currentText = ""
                val chunkSize = 2
                for (i in 0 until analysisResult.length step chunkSize) {
                    val end = (i + chunkSize).coerceAtMost(analysisResult.length)
                    currentText += analysisResult.substring(i, end)
                    chatMessages[aiMsgIndex] = ChatMessage(currentText, false)
                    delay(12)
                    listState.animateScrollToItem(chatMessages.size - 1)
                }
                chatMessages[aiMsgIndex] = ChatMessage(analysisResult, false)
                
                // Speak the analyzed prescription details asynchronously
                coroutineScope.launch {
                    com.example.data.api.ElevenLabsManager.speakText(context, analysisResult)
                }
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            com.example.data.api.ElevenLabsManager.stopSpeaking()
        }
    }

    fun sendMessage(text: String) {
        if (text.isBlank()) return
        chatMessages.add(ChatMessage(text, true))
        textInput = ""

        coroutineScope.launch {
            delay(100)
            listState.animateScrollToItem(chatMessages.size - 1)
            
            typingMessage = "Assistant is typing..."
            isTyping = true
            
            // Build the RAG Prompt with Context
            val promptWithContext = if (documentContext != null) {
                "Below is the context from the user's uploaded prescription/medical document. Use it to answer their question accurately. If the question is unrelated, answer generally.\n\n[DOCUMENT CONTEXT]:\n$documentContext\n\n[USER QUESTION]:\n$text"
            } else {
                text
            }
            
            val responseText = GeminiHealthAnalyzer.queryGrokChat(promptWithContext)
            isTyping = false
            
            // ChatGPT-style streaming typing animation!
            val aiMsgIndex = chatMessages.size
            chatMessages.add(ChatMessage("", false))
            var currentText = ""
            val chunkSize = 2
            for (i in 0 until responseText.length step chunkSize) {
                val end = (i + chunkSize).coerceAtMost(responseText.length)
                currentText += responseText.substring(i, end)
                chatMessages[aiMsgIndex] = ChatMessage(currentText, false)
                delay(12)
                listState.animateScrollToItem(chatMessages.size - 1)
            }
            chatMessages[aiMsgIndex] = ChatMessage(responseText, false)
            
            // Speak response text out loud asynchronously using ElevenLabs
            coroutineScope.launch {
                com.example.data.api.ElevenLabsManager.speakText(context, responseText)
            }
            
            delay(100)
            listState.animateScrollToItem(chatMessages.size - 1)
        }
    }

    GlassmorphicBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            // Premium Header Row (Custom, no Scaffold/TopAppBar overlay constraints)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.White.copy(alpha = 0.08f), CircleShape)
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text("AI Health Assistant", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(GlassAccent, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Online & Active", color = GlassAccent, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            // Divider Line
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color.White.copy(alpha = 0.08f))
            )

            if (documentContext != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(GlassPrimary.copy(alpha = 0.15f))
                        .border(1.dp, GlassPrimary.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Description, contentDescription = "Document", tint = GlassAccent, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Context Enabled: Prescription Active",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Clear Context",
                        tint = Color.White.copy(alpha = 0.6f),
                        modifier = Modifier
                            .size(18.dp)
                            .clickable { documentContext = null }
                    )
                }
            }

            // Chat Messages List Area (Fills the entire screen body)
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp)
            ) {
                items(chatMessages) { msg ->
                    ChatBubble(message = msg)
                }

                // Suggestion chips inside LazyColumn list so they never overlap chat bubble text
                if (chatMessages.size == 1) {
                    item {
                        androidx.compose.foundation.lazy.LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(vertical = 4.dp)
                        ) {
                            items(suggestionChips) { chip ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(Color.White.copy(alpha = 0.12f))
                                        .border(1.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(20.dp))
                                        .clickable { sendMessage(chip) }
                                        .padding(horizontal = 14.dp, vertical = 10.dp)
                                ) {
                                    Text(chip, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                }
                            }
                        }
                    }
                }

                if (isTyping) {
                    item {
                        TypingIndicator(message = typingMessage)
                    }
                }
            }

            // Text Input Field Area
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black.copy(alpha = 0.3f))
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                IconButton(
                    onClick = { imagePickerLauncher.launch("image/*") },
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color.White.copy(alpha = 0.08f), CircleShape)
                ) {
                    Icon(
                        Icons.Default.AddPhotoAlternate,
                        contentDescription = "Upload Prescription",
                        tint = Color.White
                    )
                }

                TextField(
                    value = textInput,
                    onValueChange = { textInput = it },
                    placeholder = { Text("Ask health assistant...", color = Color.White.copy(alpha = 0.5f)) },
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(28.dp))
                        .background(Color.White.copy(alpha = 0.1f)),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(onSend = { sendMessage(textInput) })
                )

                FloatingActionButton(
                    onClick = { sendMessage(textInput) },
                    containerColor = GlassPrimary,
                    contentColor = Color.White,
                    shape = CircleShape,
                    modifier = Modifier.size(50.dp)
                ) {
                    Icon(Icons.Default.Send, contentDescription = "Send")
                }
            }
        }
    }
}

@Composable
fun ChatBubble(message: ChatMessage) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val clipboardManager = androidx.compose.ui.platform.LocalClipboardManager.current
    val alignment = if (message.isUser) Alignment.CenterEnd else Alignment.CenterStart
    val bubbleColor = if (message.isUser) GlassPrimary.copy(alpha = 0.4f) else Color.White.copy(alpha = 0.12f)
    val borderColor = if (message.isUser) GlassPrimary.copy(alpha = 0.6f) else Color.White.copy(alpha = 0.2f)

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = alignment
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 300.dp)
                .clip(
                    RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (message.isUser) 16.dp else 4.dp,
                        bottomEnd = if (message.isUser) 4.dp else 16.dp
                    )
                )
                .background(bubbleColor)
                .border(
                    1.dp,
                    borderColor,
                    RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (message.isUser) 16.dp else 4.dp,
                        bottomEnd = if (message.isUser) 4.dp else 16.dp
                    )
                )
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Column {
                Text(
                    text = message.text,
                    color = Color.White,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
                if (!message.isUser && message.text.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        IconButton(
                            onClick = {
                                clipboardManager.setText(androidx.compose.ui.text.AnnotatedString(message.text))
                                android.widget.Toast.makeText(context, "Copied report details", android.widget.Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.size(22.dp)
                        ) {
                            Icon(
                                Icons.Default.ContentCopy,
                                contentDescription = "Copy message",
                                tint = Color.White.copy(alpha = 0.5f),
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TypingIndicator(message: String) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White.copy(alpha = 0.12f))
                .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                .padding(horizontal = 14.dp, vertical = 8.dp)
        ) {
            Text(
                text = message,
                color = GlassAccent,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
