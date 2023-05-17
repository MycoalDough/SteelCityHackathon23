using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class BacteriaAI : MonoBehaviour
{

    [Header("Genes")]
    public float reproductionRate;
    private float rRCT;
    public int ID; //used for memory cells 
    public float detection;
    public float healthOutside;
    public float speed;
    private Vector3 desiredPos;
    public float moveDir = 5;
    private float yPos;
    public bool replicating;
    public Cell c;

    [Header("Mutations")]
    public bool cough;
    public bool TOF;
    public bool fatigue;
    public bool SL; //surface livibility
    public bool BI; //birth infection
    // Start is called before the first frame update
    void Start()
    {
        yPos = 1.2f;
        desiredPos = new Vector3(transform.position.x + Random.Range(-moveDir, moveDir), yPos, transform.position.z + Random.Range(-moveDir, moveDir));

    }

    // Update is called once per frame
    void Update()
    {
        transform.position = Vector3.Lerp(transform.position, desiredPos, speed * Time.deltaTime);

        if (Vector3.Distance(transform.position, desiredPos) > 0.5f) return;

        desiredPos = new Vector3(transform.position.x + Random.Range(-moveDir, moveDir), yPos, transform.position.z + Random.Range(-moveDir, moveDir));
        transform.LookAt(desiredPos);
        transform.eulerAngles = new Vector3(90f, transform.rotation.y, transform.rotation.z);
    }
    
    void OnTriggerEnter(Collider col){
        if(col.gameObject.tag == "HostCell")
        {
            replicating = true;
            c = col.gameObject;
        }
    
    }
    
    public void clone()
    {
       
    }
    
    
}
