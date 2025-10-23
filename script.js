// Page navigation
function showPage(pageId){
  document.querySelectorAll('.page').forEach(p => p.classList.add('hidden'));
  document.getElementById(pageId).classList.remove('hidden');
}

// Registration
document.getElementById('registrationForm').addEventListener('submit', e=>{
  e.preventDefault();
  alert("🎉 Registration successful!");
  showPage('homePage');
});

// Posts management
let posts = [
  {id:1, content:"Welcome to Webz!", reactions:[], boosted:false}
];
function renderPosts(){
  const container = document.getElementById('postsContainer');
  container.innerHTML = '';
  posts.forEach(p=>{
    const div = document.createElement('div');
    div.classList.add('post');
    div.innerText = p.content;
    container.appendChild(div);
  });

  // Update Boost/React/Complaint selects
  const boostSelect = document.getElementById('boostSelect');
  const reactSelect = document.getElementById('reactSelect');
  const complaintSelect = document.getElementById('complaintSelect');
  [boostSelect, reactSelect, complaintSelect].forEach(sel=>{
    sel.innerHTML = '';
    posts.forEach(p=>{
      const option = document.createElement('option');
      option.value = p.id;
      option.innerText = p.content.substring(0,30)+'...';
      sel.appendChild(option);
    });
  });
}
renderPosts();

// Add new post
document.getElementById('addPostBtn').addEventListener('click', ()=>{
  const content = document.getElementById('newPost').value.trim();
  if(content){
    posts.push({id:posts.length+1, content, reactions:[], boosted:false});
    document.getElementById('newPost').value='';
    renderPosts();
  }
});

// Boost
document.getElementById('boostBtn').addEventListener('click', ()=>{
  const postId = document.getElementById('boostSelect').value;
  const amount = document.getElementById('boostAmount').value;
  if(amount>0){
    const post = posts.find(p=>p.id==postId);
    post.boosted = true;
    alert(`✅ Post boosted for R${amount}`);
  }else alert("Enter valid amount in ZAR");
});

// React
document.getElementById('reactBtn').addEventListener('click', ()=>{
  const postId = document.getElementById('reactSelect').value;
  const reaction = document.getElementById('reactionType').value;
  const post = posts.find(p=>p.id==postId);
  post.reactions.push(reaction);
  alert(`You reacted with ${reaction}`);
});

// Complaint
document.getElementById('complaintBtn').addEventListener('click', ()=>{
  const postId = document.getElementById('complaintSelect').value;
  const text = document.getElementById('complaintText').value.trim();
  if(text){
    alert(`📢 Complaint sent to NCC: "${text}"`);
    document.getElementById('complaintText').value='';
  } else alert("Enter complaint text");
});

// Navigation buttons
document.getElementById('boostPageBtn').addEventListener('click', ()=>showPage('boostPage'));
document.getElementById('homeBtn').addEventListener('click', ()=>showPage('homePage'));

// Fake CIPC & Funding
document.getElementById('cipcBtn').addEventListener('click', ()=>alert("CIPC Application submitted!"));
document.getElementById('fundingBtn').addEventListener('click', ()=>{
  const hasCIPC = confirm("Do you have a valid CIPC Certificate?");
  if(hasCIPC) alert("Government funding application submitted!");
  else alert("CIPC Certificate required first!");
});
